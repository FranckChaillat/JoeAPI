package org.joe.api.entities.dto

import java.sql.ResultSet

import scala.annotation.tailrec

final case class ReportItem(category: String, amount: Float, transactionCount: Int)
final case class ReportResponse(reportItems: Seq[ReportItem], total: Float, totalTransactionCount: Int)


object ReportResponse {

  def parse(rs: ResultSet): ReportResponse = {
    @tailrec
    def collect(acc: List[ReportItem]) : ReportResponse = {
      if (rs.next()) {
        collect(acc :+ ReportItem(
          rs.getString(1),
          rs.getFloat(2),
          rs.getInt(3)
        ))
      } else

        ReportResponse(acc, acc.foldLeft(0.0f) ((acc, report) => acc + report.amount), acc.length)
    }

    collect(List.empty[ReportItem])
  }

}