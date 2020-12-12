package org.joe.api.business

import org.joe.api.repository.{BudgetRepository, Repositories}
import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}

object BudgetService {

  def getCategories()(implicit ec: ExecutionContext) : Kleisli[Future, Repositories[BudgetRepository], List[String]] = Kleisli { repositories =>
    repositories
      .repository
      .getCategories()
      .run(repositories.build)
  }

  def addCategory(label: String, description: Option[String])(implicit ec: ExecutionContext): Kleisli[Future, Repositories[BudgetRepository], Unit] = Kleisli { repositories =>
    repositories
      .repository
      .addCategory(label, description)
      .run(repositories.build)
  }

}
