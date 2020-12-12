package org.joe.api.endpoints.transactions

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.joe.api.business.TransactionService
import org.joe.api.endpoints.BodyEndPoint
import org.joe.api.entities.dto.BulkAddTransactionRequest
import org.joe.api.repository.{Repositories, TransactionRepository}
import org.joe.api.validators.AddBulkTransactionValidator
import org.json4s.{DefaultFormats, Formats}
import scalaz.Reader

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object AddBulkTransactionEndPoint extends BodyEndPoint[BulkAddTransactionRequest, String, TransactionRepository] {

  private implicit val format: Formats = DefaultFormats
  private implicit val timeout: Duration = 10.seconds

  override def validator: Option[AddBulkTransactionValidator.type] = Some(AddBulkTransactionValidator)

  override def route()(implicit ec: ExecutionContext): Reader[Repositories[TransactionRepository], Route] = Reader { repositories =>
    post {
      path("payments" / "bulk") {
        handle { request =>
          TransactionService
            .addTransactions(request.transactions, request.limitDate)
            .run(repositories)
            .map(_ => "OK")
        }
      }
    }
  }
}
