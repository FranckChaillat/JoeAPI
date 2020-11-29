package org.joe.api.validators

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Rejection, StandardRoute}

trait Validator[T] {
  def validate(okCase: T => StandardRoute)(implicit input : T): StandardRoute = {
    check(input).map(reject(_)).getOrElse(okCase(input))
  }

  protected def check(input: T) : Option[Rejection]
}
