package org.joe.api.business

import java.text.SimpleDateFormat

import org.joe.api.entities.dto.ReportResponse
import org.joe.api.repository.Repositories
import scalaz.Kleisli

import scala.concurrent.{ExecutionContext, Future}

object ReportService {

  def getReport(accountId: Int, categories: Seq[String], startDate: Option[String], endDate: Option[String])(implicit ec: ExecutionContext) : Kleisli[Future, Repositories, ReportResponse] = Kleisli { repositories =>
    val formatter: SimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy")
    repositories
      .transactionRepository
      .getReport(accountId, categories, startDate.map(formatter.parse), endDate.map(formatter.parse))
      .run(repositories.connectionBuilder)
  }
}
