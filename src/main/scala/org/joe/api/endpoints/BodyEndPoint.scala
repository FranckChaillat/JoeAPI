package org.joe.api.endpoints

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Rejection, Route}
import org.joe.api.repository.Repositories
import org.joe.api.validators.Validator
import org.json4s.jackson.Serialization.{read, write}
import org.json4s._
import scalaz.Reader

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration

trait BodyEndPoint[TReq, TRes <: AnyRef, R] extends EndPoint[R] {
  def validator: Option[Validator[TReq]]
  def route()(implicit ec: ExecutionContext) : Reader[Repositories[R], Route]

  def handle(executor: TReq => Future[TRes])(implicit mf: Manifest[TReq], timeout: Duration, formats: Formats, ec: ExecutionContext): Route = {
    withRequestTimeout(timeout, _ => getErrorResponse) {
      entity(as[String]) { body =>
        implicit val request: TReq = read[TReq](body)
        validator
          .getOrElse(new Validator[TReq] { override protected def check(input: TReq): Option[Rejection] = None })
          .validate { req =>
          val res = executor(req)
          complete(res.map(wrapResult))
        }
      }
    }
  }

  private def wrapResult[T <: AnyRef](res: T)(implicit formats: Formats) : HttpResponse = {
    HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, write[T](res)))
  }

  private def getErrorResponse = {
    HttpResponse(StatusCode.int2StatusCode(500), entity = "Request timeout")
  }
}
