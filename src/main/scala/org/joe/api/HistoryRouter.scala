package org.joe.api

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import org.joe.api.endpoints.budgets.{AddBudget, GetBudgets}
import org.joe.api.endpoints.reports.{GetBalanceHistory, GetReport}
import org.joe.api.endpoints.transactions.{AddBulkTransactionEndPoint, AddTransactionEndPoint, GetHistoryEndPoint, UpdateTransactionEndPoint}
import org.joe.api.entities.ErrorResponse
import org.joe.api.exceptions.UpdateException
import org.joe.api.exceptions.rejections.{EmptyListRejection, InvalidValueRangeRejection}
import org.joe.api.repository.{BudgetRepository, ReportRepository, Repositories, TransactionRepository}
import org.json4s.jackson.Serialization.write
import org.json4s.{DefaultFormats, Formats}

import scala.concurrent.ExecutionContext.Implicits.global

class HistoryRouter(transactionRepository: Repositories[TransactionRepository],
                    budgetRepository: Repositories[BudgetRepository],
                    reportRepository: Repositories[ReportRepository]) {

  private implicit def format: Formats = DefaultFormats

  private implicit def serviceExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e : UpdateException =>
        complete(createErrorResponse(StatusCodes.BadRequest, Some(e.msg)))
      case t: Throwable =>
        extractUri { uri =>
          println(t.getMessage)
          println(s"Request to $uri could not be handled normally")
          complete(createErrorResponse(StatusCodes.InternalServerError, None))
        }
    }

  private implicit def rejectionHandler : RejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
        case InvalidValueRangeRejection(paramName, min, max, actual) =>
          complete(createErrorResponse(
            StatusCodes.BadRequest,
            Some(s"Invalid value for parameter $paramName, allowed between $min and $max, given $actual"))
          )
        case EmptyListRejection(paramName) =>
          complete(createErrorResponse(StatusCodes.BadRequest, Some(s"The parameter $paramName is not supposed to be empty.")))
        case e: Exception =>
          complete("")
      }
      .result()

  private def createErrorResponse(status: StatusCode, message: Option[String]) = {
    HttpResponse(status, entity = HttpEntity(ContentTypes.`application/json`, message.map(m => write(ErrorResponse(m))).getOrElse("")))
  }


  def routes: Route = pathPrefix("joe") {
    val reportRoutes = Seq(GetBalanceHistory, GetReport)
    val transactionRoutes = Seq(
      GetHistoryEndPoint,
      AddTransactionEndPoint,
      UpdateTransactionEndPoint,
      AddBulkTransactionEndPoint
    )
    val budgetRoutes = Seq(AddBudget, GetBudgets)
    val routes =
      (transactionRoutes.map(_.route.run(transactionRepository))
          ++ budgetRoutes.map(_.route.run(budgetRepository))
          ++ reportRoutes.map(_.route.run(reportRepository)))
        .reduce((a, b) => a ~ b)

    Route.seal {
      routes
    }
  }
}
