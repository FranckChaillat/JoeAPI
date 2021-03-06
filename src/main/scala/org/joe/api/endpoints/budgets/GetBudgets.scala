package org.joe.api.endpoints.budgets

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller.CsvSeq
import io.circe.Encoder
import io.circe.syntax._
import org.joe.api.business.BudgetService
import org.joe.api.endpoints.EndPoint
import org.joe.api.entities.dto.BudgetItem.budgetItemEncoder
import org.joe.api.repository.{BudgetRepository, Repositories}
import scalaz.Reader

import scala.concurrent.ExecutionContext

object GetBudgets extends EndPoint[BudgetRepository] {


  private def wrapResult[T <: AnyRef](res: T)(implicit encoder: Encoder[T]) : HttpResponse = {
    val content = res.asJson.noSpaces
    HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, content))
  }

  override def route()(implicit ec: ExecutionContext): Reader[Repositories[BudgetRepository], Route] = Reader {
    repositories =>
      get {
        path("budgets") {
          parameters(Symbol("accountId").as[Int]) { accountId =>
            val res = BudgetService.getBudgets(accountId)
              .run(repositories)
            complete(res.map(x => wrapResult(x)))
          }
        }
      }
  }
}
