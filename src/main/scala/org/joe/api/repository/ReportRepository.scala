package org.joe.api.repository

import java.sql.Connection
import java.util.Date

import org.joe.api.entities.dto.{BalanceObject, ReportResponse}
import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait ReportRepository {
  type ConnectionBuilder = () => Try[Connection]
  def getReport(accountid: Int, categories: Seq[String], startDate: Option[Date], endDate: Option[Date])(implicit ec: ExecutionContext): Kleisli[Future, ConnectionBuilder, ReportResponse]
  def getBalanceHistory(accountId: Int, from: Date, to: Date)(implicit ec: ExecutionContext): Kleisli[Future, SqLiteRepository.ConnectionBuilder, List[BalanceObject]]
  def getLastBalanceCheckPoint(accountId: Int, date: Date)(implicit ex: ExecutionContext): Kleisli[Future, SqLiteRepository.ConnectionBuilder, BalanceObject]
}
