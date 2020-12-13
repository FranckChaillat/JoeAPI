package org.joe.api.repository

import java.sql.Connection

import org.joe.api.entities.dto.BudgetItem
import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait BudgetRepository {
  type ConnectionBuilder = () => Try[Connection]
  def getBudgets(acccountId: Int)(implicit ec: ExecutionContext): Kleisli[Future, ConnectionBuilder, List[BudgetItem]]
  def addBudget(accountId: Int, label: String, description: Option[String], amount: Option[Float])(implicit ec: ExecutionContext): Kleisli[Future, ConnectionBuilder, Unit]
}
