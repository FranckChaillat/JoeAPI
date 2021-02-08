package org.joe.api.repository

import java.sql.Connection

import org.joe.api.entities.dto.BudgetItem
import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}

trait BudgetRepository {
  def getBudgets(acccountId: Int)(implicit ec: ExecutionContext): Kleisli[Future, Connection, List[BudgetItem]]
  def addBudget(accountId: Int, label: String, description: Option[String], amount: Option[Float])(implicit ec: ExecutionContext): Kleisli[Future, Connection, Unit]
}
