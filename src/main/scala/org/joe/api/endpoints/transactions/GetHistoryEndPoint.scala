package org.joe.api.endpoints.transactions

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller._
import io.circe._
import io.circe.syntax._
import org.joe.api.business.TransactionService
import org.joe.api.endpoints.EndPoint
import org.joe.api.repository.Repositories
import scalaz.Reader

import scala.concurrent.ExecutionContext

object GetHistoryEndPoint extends EndPoint {

  private def wrapResult[T <: AnyRef](res: T)(implicit encoder: Encoder[T]) : HttpResponse = {
    val content = res.asJson.noSpaces
    HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, content))
  }

  override def route()(implicit ec: ExecutionContext): Reader[Repositories, Route] = Reader {
    repositories =>
      get {
        path("payments") {
          parameters("categories".as(CsvSeq[String]) ? List.empty, Symbol("accountId").as[Int], Symbol("startDate").?, Symbol("endDate").?) {
            (categories, accountId, startDate, endDate) =>
              val res = TransactionService.getTransactionHistory(accountId, categories, startDate, endDate)
                .run(repositories)
              complete(res.map(x => wrapResult(x)))
          }
        }
      }
  }
}
