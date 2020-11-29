package org.joe.api.repository.utils
import java.sql.{Connection, PreparedStatement}

final case class Update private(table: String, predicates: List[Predicate] = List.empty, setPart: List[(String, String)] = List.empty) extends Query {

  def this(from: String) = {
    this(from, List.empty, List.empty)
  }

  def set(updateParts: (String, String)*): Update = {
    copy(predicates = List.empty, setPart = updateParts.toList)
  }

  def withPredicate(column: String, op: Operator.Value, value: String): Update = {
    copy(predicates = this.predicates.:+(Predicate(column, op, value)))
  }

  def withPredicate(column: String, op: Operator.Value, value: Option[String]): Update = {
    value.map { v =>
      copy(predicates = this.predicates.:+(Predicate(column, op, v)))
    }.getOrElse(this)
  }

  def build(connection: Connection): PreparedStatement = Update.buildQuery(this, connection)
}


object Update {

  def buildQuery(q: Update, conn: Connection): PreparedStatement = {
   val setPart = q.setPart.map(_._1)
      .map(x => s"""$x = ?""").mkString(", ")

    val buildPredicate = q.predicates
      .zipWithIndex
      .foldLeft(
        s"""UPDATE ${q.table}
           |SET $setPart
           |""".stripMargin)_

    val query = buildPredicate {
      case (acc, (Predicate(c, o, _), index)) =>
        val str: String = o
        s"""$acc
           |${if (index == 0) "WHERE " else "AND "} $c $str ?
           |""".stripMargin
    }

    val stm = conn.prepareStatement(query)

    (q.setPart.map(_._2) ++ q.predicates.map(x => x.value))
      .zipWithIndex
      .foreach {
        case (v, i) =>
         stm.setObject(i + 1, v)
      }

    stm
  }

}
