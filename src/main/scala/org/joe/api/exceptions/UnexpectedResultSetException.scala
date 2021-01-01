package org.joe.api.exceptions

final case class UnexpectedResultSetException(message: String) extends Exception(message)
