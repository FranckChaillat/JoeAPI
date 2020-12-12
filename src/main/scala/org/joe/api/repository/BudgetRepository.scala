package org.joe.api.repository

import java.sql.Connection

import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait BudgetRepository {
  type ConnectionBuilder = () => Try[Connection]
  def getCategories()(implicit ec: ExecutionContext): Kleisli[Future, ConnectionBuilder, List[String]]
  def addCategory(categoryLabel: String, categoryDescription: Option[String])(implicit ec: ExecutionContext): Kleisli[Future, ConnectionBuilder, Unit]
}
