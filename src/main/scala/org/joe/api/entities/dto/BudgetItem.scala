package org.joe.api.entities.dto

import java.sql.ResultSet

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class BudgetItem(label: String, description: Option[String], amount: Option[Float])


object BudgetItem {

  implicit val budgetItemEncoder: Encoder[BudgetItem] = deriveEncoder[BudgetItem]

  @scala.annotation.tailrec
  def parse(rs: ResultSet, acc: List[BudgetItem] = List.empty): List[BudgetItem] = {
    if(rs.next()) {
      val amount = rs.getFloat(3)
      val parsed = BudgetItem(rs.getString(1), Option(rs.getString(2)), Option.when( amount > 0)(amount))
      parse(rs, acc.+:(parsed))
    }
    else
      acc
  }
}