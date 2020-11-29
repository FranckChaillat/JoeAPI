package org.joe.api.exceptions.rejections

import akka.http.scaladsl.server.Rejection

case class InvalidValueRangeRejection[T](paramName: String, min: T, max: T, actual: T) extends Rejection
