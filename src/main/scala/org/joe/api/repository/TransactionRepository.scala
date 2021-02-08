package org.joe.api.repository

import java.sql.Connection
import java.util.Date

import org.joe.api.entities.dto.BillingRow
import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}

trait TransactionRepository {
  def updateTransaction(accountId: Int, identifier: String, category: String)(implicit ec: ExecutionContext): Kleisli[Future, Connection, Unit]

  def bulkInsertTransaction(rows: Seq[BillingRow], limitDate: Option[Date])(implicit ec: ExecutionContext): Kleisli[Future, Connection, Unit]

  def insertTransaction(row: BillingRow)(implicit ec: ExecutionContext): Kleisli[Future, Connection, Unit]

  def getTransactionRows(accountid: Int, categories: Seq[String], startDate: Option[Date], endDate: Option[Date])(implicit ec: ExecutionContext): Kleisli[Future, Connection, Seq[BillingRow]]
}
