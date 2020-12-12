package org.joe.api.endpoints

import akka.http.scaladsl.server.Route
import org.joe.api.repository.Repositories
import scalaz.Reader

import scala.concurrent.ExecutionContext

trait EndPoint[R] {
  def route()(implicit ec: ExecutionContext) : Reader[Repositories[R], Route]
}
