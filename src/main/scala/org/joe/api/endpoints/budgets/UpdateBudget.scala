package org.joe.api.endpoints.budgets

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Rejection, Route}
import org.joe.api.business.BudgetService
import org.joe.api.endpoints.BodyEndPoint
import org.joe.api.entities.dto.AddBudgetRequest
import org.joe.api.exceptions.rejections.{InvalidValueRangeRejection, RequiredFieldRejection}
import org.joe.api.repository.{BudgetRepository, Repositories}
import org.joe.api.validators.Validator
import org.json4s.{DefaultFormats, Formats}
import scalaz.Reader

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, _}

object UpdateBudget extends BodyEndPoint[AddBudgetRequest, String, BudgetRepository] {

  private implicit val format: Formats = DefaultFormats
  private implicit val timeout: Duration = 10.seconds

  override def validator: Option[Validator[AddBudgetRequest]] = Some {
    (input: AddBudgetRequest) => {
      if (input.amount.isEmpty)
        Some(RequiredFieldRejection("amount"))
      else if (input.amount.exists(_ < 0))
        Some(InvalidValueRangeRejection("amount", 0, Int.MaxValue, input.amount.getOrElse(-1)))
      else if (input.accountId < 0)
        Some(InvalidValueRangeRejection("accountId", 0, Int.MaxValue, input.accountId))
      else None
    }
  }

  override def route()(implicit ec: ExecutionContext): Reader[Repositories[BudgetRepository], Route] = Reader {
    repository =>

      options {
        complete("ok")
      } ~
        put {
          path("budgets" / ".{1,100}".r) { identifier =>
            handle { request =>
              BudgetService
                .updateBudget(identifier, request.description, request.amount)
                .run(repository)
                .map(_ => "OK")
            }
          }
        }
  }
}
