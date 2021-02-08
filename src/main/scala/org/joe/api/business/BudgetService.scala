package org.joe.api.business

import org.joe.api.entities.dto.BudgetItem
import org.joe.api.repository.{BudgetRepository, Repositories}
import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}

object BudgetService {

  def getBudgets(acccountId: Int)(implicit ec: ExecutionContext) : Kleisli[Future, Repositories[BudgetRepository], List[BudgetItem]] = Kleisli { repositories =>
    repositories
      .repository
      .getBudgets(acccountId)
      .run(repositories.connection)
  }

  def addBudget(accountId: Int, label: String, description: Option[String], amount: Option[Float])(implicit ec: ExecutionContext): Kleisli[Future, Repositories[BudgetRepository], Unit] = Kleisli { repositories =>
    repositories
      .repository
      .addBudget(accountId, label, description, amount)
      .run(repositories.connection)
  }

}
