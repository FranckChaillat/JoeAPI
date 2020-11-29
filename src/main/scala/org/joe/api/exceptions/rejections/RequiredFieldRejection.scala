package org.joe.api.exceptions.rejections

import akka.http.scaladsl.server.Rejection

case class RequiredFieldRejection(field: String, message: Option[String] = None) extends Rejection
