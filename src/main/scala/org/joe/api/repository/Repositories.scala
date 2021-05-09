package org.joe.api.repository

import java.sql.Connection
import scala.concurrent.Future
import scala.util.Try

trait Repositories[T] {
  def repository: T
  def connection: Connection
}
