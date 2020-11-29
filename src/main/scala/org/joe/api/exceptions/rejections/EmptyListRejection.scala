package org.joe.api.exceptions.rejections

import akka.http.scaladsl.server.Rejection

case class EmptyListRejection(paramName: String) extends Rejection
