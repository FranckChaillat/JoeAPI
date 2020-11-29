package org.joe.api.exceptions

final case class SqlLiteConnectionError(database: String) extends Exception(s"Unable to conect to sqllite database ($database)")
