package org.joe.api

import java.sql.{Connection, DriverManager}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import org.joe.api.configuration.Configuration
import org.joe.api.exceptions.SqlLiteConnectionError
import org.joe.api.repository.{Repositories, SqLiteRepository, TransactionRepository}
import org.json4s.{DefaultFormats, Formats}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.util.{Failure, Try}

object Main  {

  implicit val config: Config = ConfigFactory.load()
  val Configuration(connectionString, host, port) = Configuration.getConfiguration()

  private def getRepositories: Repositories = new Repositories {
    override def transactionRepository: TransactionRepository = SqLiteRepository
    override def connectionBuilder: () => Try[Connection] = () => {
      Try {
        DriverManager.getConnection(connectionString)
      }.recoverWith {
        case _: Throwable =>
          Failure(SqlLiteConnectionError(connectionString))
      }
    }
  }

  implicit val system: ActorSystem = ActorSystem("joeapi")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val jsonFormats: Formats = DefaultFormats


  def main(args: Array[String]): Unit = {
    val repository : Repositories = getRepositories
    lazy val router = new HistoryRouter(repository)
    val bindingFuture = Http().bindAndHandle(router.routes, host, port)
    println(s"Server online at http://${host}:${port}\nPress RETURN to stop...")
    system.registerOnTermination(() => bindingFuture.flatMap(_.unbind()))
    Await.result(system.whenTerminated, Duration.Inf)
  }


}
