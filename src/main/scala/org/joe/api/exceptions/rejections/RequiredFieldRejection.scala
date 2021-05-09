package org.joe.api.exceptions.rejections

import akka.http.scaladsl.server.Rejection

final case class RequiredFieldRejection(field: String, message: Option[String] = None) extends Rejection
