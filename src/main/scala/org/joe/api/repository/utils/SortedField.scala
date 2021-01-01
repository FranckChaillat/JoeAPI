package org.joe.api.repository.utils

final case class SortedField(field: String, reversed: Boolean)

object SortedField {
  def asc(field: String): SortedField =
    SortedField(field, reversed = false)

  def desc(field: String): SortedField =
    SortedField(field, reversed = true)

}