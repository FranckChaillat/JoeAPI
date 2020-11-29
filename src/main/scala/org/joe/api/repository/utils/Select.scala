package org.joe.api.repository.utils

import java.sql.{Connection, PreparedStatement}

final case class Select private (table: String, fields: List[String] = List.empty, predicates: List[Predicate] = List.empty, groupedFields: List[String] = List.empty) extends Query {

  def this(from: String) = {
    this(from, List.empty, List.empty)
  }

  def withFields(columns: String*): Select =
    copy(fields = columns.toList)

  def withPredicate(column: String, op: Operator.Value, value: Any): Select = {
    copy(predicates = this.predicates.:+(Predicate(column, op, value)))
  }

  def withPredicate(column: String, op: Operator.Value, value: Option[Any]): Select = {
    value.map { v =>
      copy(predicates = this.predicates.:+(Predicate(column, op, v)))
    }.getOrElse(this)
  }

  def grouped(fields: String*): Select = {
    copy(groupedFields = fields.toList)
  }

  override def build(connection: Connection): PreparedStatement = {
    Select.buildQuery(this, connection)
  }
}

object Select {

  def buildQuery(q: Select, conn: Connection): PreparedStatement = {
    val buildPredicate = q.predicates
      .zipWithIndex
      .foldLeft(
        s"""SELECT ${q.fields.mkString(", ")}
           |FROM ${q.table}""".stripMargin)_

    val query = buildPredicate {
      case (acc, (Predicate(c, o, _), index)) =>
        val str: String = o
        s"""$acc
           |${if (index == 0) "WHERE " else "AND "} $c $str
           |""".stripMargin
    }

    val stm = if(q.groupedFields.nonEmpty) {
      conn.prepareStatement(
        s"""$query
           |GROUP BY ${q.groupedFields.mkString(", ")}"""
          .stripMargin)
      } else conn.prepareStatement(query)

    q.predicates.zipWithIndex.foreach {
      case (p, i) => stm.setObject(i + 1, p.value)
    }

    stm
  }
}