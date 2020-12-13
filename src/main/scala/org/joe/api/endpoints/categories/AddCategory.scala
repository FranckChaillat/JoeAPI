package org.joe.api.endpoints.categories

import akka.http.scaladsl.model.headers.`Access-Control-Allow-Origin`
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.joe.api.business.BudgetService
import org.joe.api.endpoints.BodyEndPoint
import org.joe.api.entities.dto.AddCategoryRequest
import org.joe.api.repository.{BudgetRepository, Repositories}
import org.joe.api.validators.Validator
import org.json4s.{DefaultFormats, Formats}
import scalaz.Reader

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object AddCategory extends BodyEndPoint[AddCategoryRequest, String, BudgetRepository] {

  private implicit val format: Formats = DefaultFormats
  private implicit val timeout: Duration = 10.seconds

  override def validator: Option[Validator[AddCategoryRequest]] = None
  override def route()(implicit ec: ExecutionContext): Reader[Repositories[BudgetRepository], Route] = Reader {
    repositories =>
      respondWithHeaders(`Access-Control-Allow-Origin`.*) {
        post {
          path("budget" / "categories") {
            handle { request =>
              BudgetService
                .addCategory(request.categoryLabel, request.categoryDescription)
                .run(repositories)
                .map(_ => "OK")
            }
          }
        }
      }
  }
}
