package org.joe.api.business

import java.text.SimpleDateFormat

import org.joe.api.entities.dto.ReportResponse
import org.joe.api.repository.{Repositories, TransactionRepository}
import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}

object ReportService {

  def getReport(accountId: Int, categories: Seq[String], startDate: Option[String], endDate: Option[String])(implicit ec: ExecutionContext) : Kleisli[Future, Repositories[TransactionRepository], ReportResponse] = Kleisli { repositories =>
    val formatter: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    repositories
        .repository
        .getReport(accountId, categories, startDate.map(formatter.parse), endDate.map(formatter.parse))
        .run(repositories.build)
  }
}
