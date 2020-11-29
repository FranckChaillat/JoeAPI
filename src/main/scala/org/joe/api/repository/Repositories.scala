package org.joe.api.repository

import java.sql.Connection

import scala.util.Try

trait Repositories {
  def transactionRepository: TransactionRepository
  def connectionBuilder: () => Try[Connection]
}
