package org.joe.api.endpoints.budgets

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.joe.api.business.BudgetService
import org.joe.api.endpoints.BodyEndPoint
import org.joe.api.entities.dto.AddBudgetRequest
import org.joe.api.repository.{BudgetRepository, Repositories}
import org.joe.api.validators.Validator
import org.json4s.{DefaultFormats, Formats}
import scalaz.Reader

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object AddBudget extends BodyEndPoint[AddBudgetRequest, String, BudgetRepository] {

  private implicit val format: Formats = DefaultFormats
  private implicit val timeout: Duration = 10.seconds

  override def validator: Option[Validator[AddBudgetRequest]] = None

  override def route()(implicit ec: ExecutionContext): Reader[Repositories[BudgetRepository], Route] = Reader {
    repositories =>
      options {
        complete("ok")
      } ~
      post {
        path("budgets") {
          handle { request =>
            BudgetService
              .addBudget(request.accountId, request.label, request.description, request.amount)
              .run(repositories)
              .map(_ => "OK")
          }
        }
      }
  }
}
