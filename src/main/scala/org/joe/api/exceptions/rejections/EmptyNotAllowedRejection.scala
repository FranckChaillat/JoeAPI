package org.joe.api.exceptions.rejections

import akka.http.scaladsl.server.Rejection

case class EmptyNotAllowedRejection(field: String) extends Rejection
