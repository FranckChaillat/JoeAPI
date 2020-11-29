//package org.joe.api.endpoints
//
//import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
//import akka.http.scaladsl.server.Route
//import org.joe.api.entities.dto.AddTransactionRequest
//import org.joe.api.repository.Repositories
//import org.json4s.Formats
//import org.json4s.jackson.Serialization.write
//import scalaz.{Kleisli, Reader}
//
//import scala.concurrent.{ExecutionContext, Future}
//
//trait ExperimentalEndPoint[D, O <: AnyRef] {
//
//  private def wrapResult(res: O)(implicit formats: Formats) : HttpResponse = {
//    HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, write[T](res)))
//  }
//
//  def route()(implicit ec: ExecutionContext, formats: Formats): Route = Reader {
//    dependency =>
//      routeExpression()
//        .run(dependency)
//        .map(wrapResult)
//
//  }
//
//  def routeExpression(): Kleisli[Future, D, O]
//}
