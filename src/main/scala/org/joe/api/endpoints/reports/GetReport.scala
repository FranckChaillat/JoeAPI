package org.joe.api.endpoints.reports

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller._
import org.joe.api.business.ReportService
import org.joe.api.endpoints.EndPoint
import org.joe.api.repository.{ReportRepository, Repositories, TransactionRepository}
import org.json4s.jackson.Serialization.write
import org.json4s.{DefaultFormats, Formats}
import scalaz.Reader

import scala.concurrent.ExecutionContext

object GetReport extends EndPoint[ReportRepository] {

  private implicit val format: Formats = DefaultFormats

  private def wrapResult[T <: AnyRef](res: T) : HttpResponse = {
    HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, write[T](res)))
  }

  override def route()(implicit ec: ExecutionContext): Reader[Repositories[ReportRepository], Route] = Reader {
    repositories =>
      get {
        path("reports") {
          parameters("categories".as(CsvSeq[String]) ? List.empty, Symbol("accountId").as[Int], Symbol("startDate").?, Symbol("endDate").?) {
            (categories, accountId, startDate, endDate) =>
              val res = ReportService.getReport(accountId, categories, startDate, endDate)
                .run(repositories)

              complete(res.map(x => wrapResult(x)))
          }
        }
      }

  }
}
