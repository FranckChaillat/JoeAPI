package org.joe.api.repository

import java.sql.Connection
import java.util.Date

import org.joe.api.entities.dto.{BillingRow, ReportResponse}
import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait TransactionRepository {
  type ConnectionBuilder = () => Try[Connection]
  def updateTransaction(accountId: Int, identifier: String, category: String)(implicit ec: ExecutionContext): Kleisli[Future, ConnectionBuilder, Unit]
  def bulkInsertTransaction(rows : Seq[BillingRow], limitDate: Option[Date])(implicit ec: ExecutionContext) : Kleisli[Future, ConnectionBuilder, Unit]
  def insertTransaction(row: BillingRow)(implicit ec: ExecutionContext): Kleisli[Future, ConnectionBuilder, Unit]
  def getTransactionRows(accountid: Int, categories: Seq[String], startDate: Option[Date], endDate: Option[Date])(implicit ec: ExecutionContext) : Kleisli[Future, ConnectionBuilder, Seq[BillingRow]]
}
