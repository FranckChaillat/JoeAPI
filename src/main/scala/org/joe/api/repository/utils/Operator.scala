package org.joe.api.repository.utils

import scala.language.implicitConversions

object Operator extends Enumeration {
  type Operator = Value
  val ltEq, gtEq, gt, lt, eq, neq, in = Value

  def apply(operator: Operator, value: String) = {
    operator match {
      case Operator.gt | Operator.neq | Operator.eq | Operator.lt =>
        val str: String = operator
        s"$str $value"
      case Operator.in =>
        s" IN($value) "
    }
  }

  implicit def operator2String(o: Operator.Value): String = {
    o match {
      case Operator.ltEq => " <= ?"
      case Operator.gtEq => " >= ?"
      case Operator.gt => " > ?"
      case Operator.lt => " < ?"
      case Operator.eq => " = ?"
      case Operator.neq => " <> ? "
      case Operator.in  => " IN(?) "
    }
  }
}