package org.joe.api.dataaccess.requests

import java.sql.Connection

import scala.concurrent.Future

case class ExecuteQuery[U](action: Connection => Future[U])
