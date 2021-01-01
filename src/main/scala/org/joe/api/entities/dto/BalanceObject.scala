package org.joe.api.entities.dto

import java.sql.ResultSet

final case class BalanceObject(date: String, balance: Float)

object BalanceObject {
  @scala.annotation.tailrec
  def parse(rs: ResultSet, acc: List[BalanceObject] = List.empty): List[BalanceObject] = {
    if(rs.next()) {
      val newAcc = acc.+:(BalanceObject(rs.getString(1), rs.getFloat(2)))
      parse(rs, newAcc)
    }
    else acc.reverse

  }
}