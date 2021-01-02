package org.joe.api.business

import java.text.SimpleDateFormat

import org.joe.api.entities.dto.{BalanceObject, ReportResponse}
import org.joe.api.repository.{ReportRepository, Repositories}
import scalaz.Kleisli
import scalaz.std.scalaFuture._

import scala.concurrent.{ExecutionContext, Future}

object ReportService {

  def getBalanceHistory(accountId: Int, startDate: String, endDate: String)(implicit ec: ExecutionContext) : Kleisli[Future, Repositories[ReportRepository], List[BalanceObject]] = Kleisli {
    repositories =>9
      val formatter: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
       repositories.repository.getLastBalanceCheckPoint(accountId, formatter.parse(startDate))
        .flatMap { checkPoint =>
          val balanceHistory = repositories.repository.getBalanceHistory(accountId, formatter.parse(checkPoint.date), formatter.parse(endDate))
          balanceHistory.map(a => computeBalance(checkPoint, a))
        }.run(repositories.build)
  }

  def getReport(accountId: Int, categories: Seq[String], startDate: Option[String], endDate: Option[String])(implicit ec: ExecutionContext) : Kleisli[Future, Repositories[ReportRepository], ReportResponse] = Kleisli { repositories =>
    val formatter: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    repositories
        .repository
        .getReport(accountId, categories, startDate.map(formatter.parse), endDate.map(formatter.parse))
        .run(repositories.build)
  }

  private def computeBalance(checkPoint: BalanceObject, balanceHistory: List[BalanceObject]) = {
    balanceHistory.foldLeft(List(checkPoint)) {
      case (acc, e) =>
        val updated = acc.headOption.map(lastBalance => e.copy(balance = lastBalance.balance + e.balance))
            .getOrElse(e)
        updated :: acc
    }
  }
}
