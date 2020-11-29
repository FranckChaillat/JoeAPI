package org.joe.api.endpoints.categories

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.joe.api.endpoints.BodyEndPoint
import org.joe.api.entities.dto.AddCategoryRequest
import org.joe.api.repository.Repositories
import org.joe.api.validators.Validator
import org.json4s.{DefaultFormats, Formats}
import scalaz.Reader

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object AddCategory extends BodyEndPoint[AddCategoryRequest, String] {

  private implicit val format: Formats = DefaultFormats
  private implicit val timeout: Duration = 10.seconds

  override def validator: Option[Validator[AddCategoryRequest]] = None
  override def route()(implicit ec: ExecutionContext): Reader[Repositories, Route] = Reader {
    repositories =>
      post {
        handle { request =>
          Future("")
        }
      }
  }
}
