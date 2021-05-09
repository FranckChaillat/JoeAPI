package org.joe.api.business

import org.joe.api.entities.dto.BudgetItem
import org.joe.api.exceptions.ItemDuplicateError
import org.joe.api.repository.{BudgetRepository, Repositories}
import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}

object BudgetService {

  def getBudgets(accountId: Int)(implicit ec: ExecutionContext) : Kleisli[Future, Repositories[BudgetRepository], List[BudgetItem]] = Kleisli { repositories =>
    repositories
      .repository
      .getBudgets(accountId)
      .run(repositories.connection)
  }

  def addBudget(accountId: Int, label: String, description: Option[String], amount: Option[Float])(implicit ec: ExecutionContext): Kleisli[Future, Repositories[BudgetRepository], Unit] = Kleisli { repositories =>
    val budgets = getBudgets(accountId)
    budgets.run(repositories)
      .flatMap { budgets =>
        if(budgets.exists(_.label == label))
          Future.failed(ItemDuplicateError(s"Budget labeled $label already exists."))
        else
          repositories
            .repository
            .addBudget(accountId, label, description, amount)
            .run(repositories.connection)
      }
  }

  def updateBudget(budgetLabel: String, description: Option[String], amount: Option[Float])(implicit ec: ExecutionContext): Kleisli[Future, Repositories[BudgetRepository], Unit] = Kleisli { repositories =>
    if(description.nonEmpty || amount.nonEmpty) {
      repositories
        .repository
        .updateBudget(budgetLabel, description, amount)
        .run(repositories.connection)
    } else Future.unit
  }
}
