package org.joe.api.entities.dto

import java.sql.{PreparedStatement, ResultSet}
import java.text.SimpleDateFormat
import java.util.Date

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax._

import scala.annotation.tailrec

final case class BillingRow(identifier: String,
                      accountId: Int,
                      operationDate: Date,
                      valueDate: Date,
                      label: String,
                      amount: Float,
                      occurence: Option[Int] = Some(1),
                      category: Option[String])

object BillingRow {

  private val fmt = new SimpleDateFormat("yyyy-MM-dd")
  implicit val dateTimeEncoder: Encoder[Date] = Encoder.instance(a => fmt.format(a).asJson)

  implicit val billingEncoder: Encoder[BillingRow] = deriveEncoder[BillingRow]

  @tailrec
  def parse(rs: ResultSet, acc: List[BillingRow]): List[BillingRow] = {
    if(rs.next()) {
      parse(rs, acc.:+(BillingRow(
        rs.getString(1),
        rs.getInt(2),
        rs.getDate(3),
        rs.getDate(4),
        rs.getString(5),
        rs.getFloat(6),
        if(rs.getObject(7) == null) None else Some(rs.getObject(7).asInstanceOf[Int]),
        if(rs.getObject(8) == null) None else Some(rs.getObject(8).asInstanceOf[String])
      )))
    } else {
      acc
    }

  }

  def preparedStatement(row: BillingRow, stm: PreparedStatement): PreparedStatement = {
    val BillingRow(id, accountId, operationDate, valueDate, label, amount, occurence, category) = row
    stm.setString(1, id)
    stm.setInt(2, accountId)
    stm.setDate(3, new java.sql.Date(operationDate.getTime))
    stm.setDate(4, new java.sql.Date(valueDate.getTime))
    stm.setString(5, label)
    stm.setFloat(6, amount)
    occurence match { case Some(v) => stm.setInt(7, v) case _ => stm.setNull(7, java.sql.Types.INTEGER) }
    category match { case Some(v) => stm.setString(8, v) case _ => stm.setNull(8, java.sql.Types.VARCHAR) }
    stm
  }

  lazy val requiredFields: Set[String] = {
    Set("accountId", "operationDate", "valueDate", "label", "amount", "identifier")
  }
}