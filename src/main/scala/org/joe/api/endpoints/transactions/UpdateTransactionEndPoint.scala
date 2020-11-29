package org.joe.api.endpoints.transactions

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.joe.api.business.TransactionService
import org.joe.api.endpoints.BodyEndPoint
import org.joe.api.entities.dto.TransactionUpdateRequest
import org.joe.api.repository.Repositories
import org.joe.api.validators.{UpdateTransactionValidator, Validator}
import org.json4s.{DefaultFormats, Formats}
import scalaz.Reader

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, _}


object UpdateTransactionEndPoint extends BodyEndPoint[TransactionUpdateRequest, String] {

  private implicit val format: Formats = DefaultFormats
  private implicit val timeout: Duration = 10.seconds

  override def validator: Option[Validator[TransactionUpdateRequest]] =
    Some(UpdateTransactionValidator)

  //TODO: properly implement CORS
  def route()(implicit executionContext : ExecutionContext): Reader[Repositories, Route] = Reader {
    repository =>
        path("payments" / ".{32}".r) { identifier =>
          options {
            complete("ok")
          } ~
          put {
            handle { request =>
              TransactionService
                .updateTransaction(request.accountId, identifier, request.category)
                .run(repository)
                .map(_ => "OK")
            }
          }
        }
}
}
