package org.joe.api.repository.utils

import java.sql.{Connection, PreparedStatement}

import Operator._

final case class Predicate private (column: String, operator: Operator, value: Any)
trait Query {
  def predicates: List[Predicate]
  def build(connection: Connection): PreparedStatement
}

