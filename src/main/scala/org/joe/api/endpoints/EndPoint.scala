package org.joe.api.endpoints

import akka.http.scaladsl.server.Route
import org.joe.api.repository.Repositories
import scalaz.Reader

import scala.concurrent.ExecutionContext

trait EndPoint {
  def route()(implicit ec: ExecutionContext) : Reader[Repositories, Route]
}
