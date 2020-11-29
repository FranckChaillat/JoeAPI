package org.joe.api.endpoints.transactions

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.joe.api.business.TransactionService
import org.joe.api.endpoints.BodyEndPoint
import org.joe.api.entities.dto.AddTransactionRequest
import org.joe.api.repository.Repositories
import org.joe.api.validators.{AddPaymentValidator, Validator}
import org.json4s.{DefaultFormats, Formats}
import scalaz.Reader

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, _}

object AddTransactionEndPoint extends BodyEndPoint[AddTransactionRequest, String] {

  private implicit val format: Formats = DefaultFormats
  private implicit val timeout: Duration = 10.seconds

  override def validator: Option[Validator[AddTransactionRequest]] = Some(AddPaymentValidator)

  override def route()(implicit ec: ExecutionContext): Reader[Repositories, Route] = Reader {
    repositories =>
      post {
        path("payments") {
          handle { request =>
            TransactionService
              .addTransaction(
                request.accountId,
                request.category,
                request.operationDate,
                request.valueDate,
                request.amount,
                request.label)
              .run(repositories)
              .map(_ => "OK")
          }
        }
      }
  }

}
