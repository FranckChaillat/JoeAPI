package org.joe.api.repository

import java.sql
import java.sql.Connection
import java.text.SimpleDateFormat
import java.util.Date

import org.joe.api.entities.dto.{BalanceObject, BillingRow, BudgetItem, ReportResponse}
import org.joe.api.exceptions.UnexpectedResultSetException
import org.joe.api.repository.utils.Operator._
import scalaz.Kleisli
import org.joe.api.repository.utils.{Operator, Select, Update}
import org.joe.api.repository.utils.SortedField._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.chaining._

object SqLiteRepository extends TransactionRepository with BudgetRepository with ReportRepository {

  override type ConnectionBuilder = () => Try[Connection]

  override def updateTransaction(accountId: Int, identifier: String, category: String)(implicit ec: ExecutionContext): Kleisli[Future, ConnectionBuilder, Unit] = Kleisli {
    connectionBuilder =>
      Future.fromTry(connectionBuilder())
          .map { c =>
            val stm = Update("TRANSACTIONS")
                .set("category" -> s"$category")
                .withPredicate("accountId", Operator.eq, accountId.toString)
                .withPredicate("identifier", Operator.eq, s"$identifier")
                .build(c)
            stm.executeUpdate()
            c.close()
          }
  }

  override def bulkInsertTransaction(rows: Seq[BillingRow], limitDate: Option[Date])(implicit ec: ExecutionContext): Kleisli[Future, SqLiteRepository.ConnectionBuilder, Unit] = Kleisli {
    connectionBuilder =>
      Future.fromTry(connectionBuilder())
        .map { c =>
          c.setAutoCommit(false)
          limitDate.foreach { d =>
            val stm = c.prepareStatement(
              """
                |DELETE FROM TRANSACTIONS WHERE operationDate >= ?
                |""".stripMargin)

            stm.setDate(1, new sql.Date(d.getTime))
            stm.executeUpdate()
          }

          val stm = c.prepareStatement("""INSERT INTO TRANSACTIONS(identifier, accountId, operationDate, valueDate, label, amount, occurence, category)
                                         |VALUES(?, ?, ?, ?, ?, ?, ?, ?)""".stripMargin)
          rows.foreach { row =>
            BillingRow.preparedStatement(row, stm)
            stm.addBatch()
          }
          stm.executeBatch()
          c.commit()
          c.close()
        }
  }

  override def insertTransaction(row: BillingRow)(implicit ec: ExecutionContext): Kleisli[Future, SqLiteRepository.ConnectionBuilder, Unit] = Kleisli {
    connectionBuilder =>
      Future.fromTry(connectionBuilder())
        .map { c =>
          val stm = c.prepareStatement(
            """INSERT INTO TRANSACTIONS(identifier, accountId, operationDate, valueDate, label, amount, occurence, category)
              |VALUES(?, ?, ?, ?, ?, ?, ?, ?)""".stripMargin
          )
          BillingRow.preparedStatement(row, stm).execute()
          c.close()
        }
  }

  override def getTransactionRows(accountid: Int, categories: Seq[String], startDate: Option[Date], endDate: Option[Date])(implicit ec: ExecutionContext): Kleisli[Future, SqLiteRepository.ConnectionBuilder, Seq[BillingRow]] = Kleisli {
    connectionBuilder =>
      Future.fromTry(connectionBuilder())
        .map { c =>
          //val catFilterValue = Option.when(categories.nonEmpty)(categories.map(c => s"'$c'").mkString(","))
          val startDateFilterValue = startDate.map(x => new sql.Date(x.getTime))
          val endDateFilterValue = endDate.map(x => new sql.Date(x.getTime))

          val stm = Select("TRANSACTIONS")
            .withFields("*")
            .withPredicate("accountId", Operator.eq, accountid.toString)
            //.withPredicate("category", in, c.createArrayOf("VARCHAR",
            .withPredicate("operationDate", gtEq, startDateFilterValue)
            .withPredicate("operationDate", ltEq, endDateFilterValue)
            .sorted(asc("operationDate"))
            .build(c)

          stm.executeQuery()
            .pipe(x => BillingRow.parse(x, List.empty))
            .tap(_ => c.close())
        }
  }

  override def getReport(accountid: Int, categories: Seq[String], startDate: Option[Date], endDate: Option[Date])(implicit ec: ExecutionContext): Kleisli[Future, SqLiteRepository.ConnectionBuilder, ReportResponse] = Kleisli {
    connectionBuilder =>
      Future.fromTry(connectionBuilder())
        .map { c =>
          val stm = Select("TRANSACTIONS")
              .withFields("category", "SUM(amount) * -1 as amount", "COUNT(identifier)")
              .withPredicate("accountId", Operator.eq, accountid.toString)
              .withPredicate("operationDate", Operator.gt, startDate.map(d => new sql.Date(d.getTime)))
              .withPredicate("operationDate", Operator.lt, endDate.map(d => new sql.Date(d.getTime)))
              .withPredicate("amount", Operator.lt, "0")
             // .withPredicate("category", Operator.in, categories.map(x => s"$x").mkString(","))
              .grouped("category")
              .build(c)

          stm.executeQuery()
            .pipe(ReportResponse.parse)
            .tap(_ => c.close())
        }

  }


  /**Budget management methods**/
  override def getBudgets(accountId: Int)(implicit ec: ExecutionContext): Kleisli[Future, SqLiteRepository.ConnectionBuilder, List[BudgetItem]] = Kleisli {
    connectionBuilder =>
      Future.fromTry(connectionBuilder())
        .map { c =>
          val stm = Select("BUDGETS")
            .withFields("label", "description", "amount")
            .withPredicate("accountId", Operator.eq, accountId)
            .build(c)

          stm.executeQuery()
            .pipe(rs => BudgetItem.parse(rs))
            .tap(_ => c.close())
        }
  }

  override def addBudget(accountId: Int, label: String, description: Option[String], amount: Option[Float])(implicit ec: ExecutionContext): Kleisli[Future, SqLiteRepository.ConnectionBuilder, Unit] = Kleisli {
    connectionBuilder =>
      Future.fromTry(connectionBuilder())
        .map { c =>
          val stm = c.prepareStatement("INSERT INTO BUDGETS VALUES(?, ?, ?, ?)")
          stm.setInt(1, accountId)
          stm.setString(2, label)
          stm.setString(3, description.orNull)
          stm.setFloat(4, amount.getOrElse(-1.0f))
          stm.executeUpdate()

          c.close()
        }
  }

  override def getBalanceHistory(accountId: Int, from: Date, to: Date)(implicit ec: ExecutionContext): Kleisli[Future, ConnectionBuilder, List[BalanceObject]] = Kleisli {
    connectionBuilder =>
      Future.fromTry(connectionBuilder())
        .map { c =>
            val fmt = new SimpleDateFormat("yyyy-MM-dd")
            val stm = Select("TRANSACTIONS")
              .withFields("date(operationDate/1000, 'unixepoch')", "SUM(amount)")
              .withPredicate("date(operationDate/1000, 'unixepoch')", Operator.gt, fmt.format(from))
              .withPredicate("date(operationDate/1000, 'unixepoch')", Operator.lt, fmt.format(to))// new sql.Date(to.getTime))
              .withPredicate("accountId", Operator.eq, accountId)
              .grouped("date(operationDate/1000, 'unixepoch')")
              .sorted(asc("date(operationDate/1000, 'unixepoch')"))
              .build(c)

            BalanceObject.parse(stm.executeQuery())
            .tap(_ => c.close())
        }
  }

  override def getLastBalanceCheckPoint(accountId: Int, date: Date)(implicit ec: ExecutionContext): Kleisli[Future, ConnectionBuilder, BalanceObject] = Kleisli {
    connectionBuilder =>
      Future.fromTry(connectionBuilder())
        .flatMap { c =>
          val fmt = new SimpleDateFormat("yyyy-MM-dd")
          val stm = Select("BALANCE_CHECKPOINT")
            .withFields("checkPointDate", "amount")
            .withPredicate("accountId", Operator.eq, accountId)
            .withPredicate("checkPointDate", Operator.ltEq, fmt.format(date))
            .sorted(desc("checkPointDate"))
            .limit(1)
            .build(c)


          val rs = stm.executeQuery()
          if(rs.next()) {
            val balanceObject = BalanceObject(rs.getString(1), rs.getFloat(2))
            Future.successful(balanceObject)
          }
          else
            Future.failed(UnexpectedResultSetException("No row found in Balance check point resultset"))
        }
  }
}
