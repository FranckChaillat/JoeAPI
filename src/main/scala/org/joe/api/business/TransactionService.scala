package org.joe.api.business

import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.text.{DateFormat, SimpleDateFormat}
import java.util.Date

import javax.xml.bind.DatatypeConverter
import org.joe.api.entities.dto.{AddTransactionRequest, BillingRow}
import org.joe.api.repository.Repositories
import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object TransactionService {

  def addTransactions(transactions: Seq[AddTransactionRequest], limitDate: Option[String])(implicit ec: ExecutionContext): Kleisli[Future, Repositories, Unit] = Kleisli {
    repository =>
      implicit val formatter: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
      val billingRows = transactions.map(getBillingRow)
      repository.transactionRepository
        .bulkInsertTransaction(billingRows, limitDate.map(formatter.parse))
        .run(repository.connectionBuilder)
  }

  def updateTransaction(accountId: Int, identifier: String, category: Option[String])(implicit ec: ExecutionContext): Kleisli[Future, Repositories, Unit] = Kleisli {
    repository =>
      category.map(c =>
        repository.transactionRepository.updateTransaction(accountId, identifier, c)
        .run(repository.connectionBuilder))
        .getOrElse(Future.unit)
  }

  def addTransaction(accountId: Int, category: Option[String],
                     operationDate: String, valueDate: String,
                     amount: Float, label: String)(implicit ec: ExecutionContext): Kleisli[Future, Repositories, Unit] =
    Kleisli { repository =>
      val formatter = new SimpleDateFormat("yyyy-MM-dd")
      val result = for {
        od <- Try(formatter.parse(operationDate))
        vd <- Try(formatter.parse(valueDate))
      } yield {
        val id = generateIdentifier(od, vd, label, amount)
        val row = BillingRow(id, accountId, od, vd, label, amount, Some(1), category)
        repository
          .transactionRepository.insertTransaction(row)
          .run(repository.connectionBuilder)
      }
      Future.fromTry(result).flatten
    }

  def getTransactionHistory(accountId: Int, categories: Seq[String], startDate: Option[String], endDate: Option[String])
                           (implicit ec: ExecutionContext) : Kleisli[Future, Repositories, Seq[BillingRow]] = Kleisli { repository =>
      val formatter = new SimpleDateFormat("yyyy-MM-dd")
      val result = for {
        sd <- Try(startDate.map(formatter.parse))
        ed <- Try(endDate.map(formatter.parse))
      } yield {
        repository
          .transactionRepository
          .getTransactionRows(accountId, categories, sd, ed).run(repository.connectionBuilder)
      }
      Future.fromTry(result).flatten
    }

  private def generateIdentifier(operationDate: Date, valueDate: Date, label: String, amount: Float) = {
    import java.security.MessageDigest
    val md = MessageDigest.getInstance("MD5")
    val str = Seq(
      new Timestamp(operationDate.getTime).toString,
      new Timestamp(valueDate.getTime).toString,
      label,
      amount).mkString(";")

    val hashBytes = md.digest(str.getBytes(StandardCharsets.UTF_8))
    DatatypeConverter.printHexBinary(hashBytes).toUpperCase()
  }

  private def getBillingRow(transactionRequest: AddTransactionRequest)(implicit fmt: DateFormat): BillingRow = {
    val od = fmt.parse(transactionRequest.operationDate)
    val vd = fmt.parse(transactionRequest.valueDate)
    val identifier = generateIdentifier(od, vd, transactionRequest.label, transactionRequest.amount)
    BillingRow(identifier, transactionRequest.accountId, od, vd, transactionRequest.label, transactionRequest.amount, Some(1), transactionRequest.category)
  }
}
