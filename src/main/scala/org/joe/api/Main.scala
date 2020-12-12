package org.joe.api

import java.sql.{Connection, DriverManager}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import org.joe.api.configuration.Configuration
import org.joe.api.exceptions.SqlLiteConnectionError
import org.joe.api.repository.{BudgetRepository, Repositories, SqLiteRepository, TransactionRepository}
import org.json4s.{DefaultFormats, Formats}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.util.{Failure, Try}

object Main  {

  implicit val config: Config = ConfigFactory.load()
  val Configuration(connectionString, host, port) = Configuration.getConfiguration()


  private def sqliteConnectionBuilder: () => Try[Connection] = () => {
    Try {
      DriverManager.getConnection(connectionString)
    }.recoverWith {
      case _: Throwable =>
        Failure(SqlLiteConnectionError(connectionString))
    }
  }

  private def getTransactionRepository: Repositories[TransactionRepository] = new Repositories[TransactionRepository] {
    def repository: TransactionRepository = SqLiteRepository
    def build: () => Try[Connection] = sqliteConnectionBuilder
  }

  private def getBudgetRepository = new Repositories[BudgetRepository] {
    def repository: BudgetRepository = SqLiteRepository
    def build: () => Try[Connection] = sqliteConnectionBuilder
  }

  implicit val system: ActorSystem = ActorSystem("joeapi")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val jsonFormats: Formats = DefaultFormats


  def main(args: Array[String]): Unit = {
    lazy val router = new HistoryRouter(getTransactionRepository, getBudgetRepository)
    val bindingFuture = Http().bindAndHandle(router.routes, host, port)
    println(s"Server online at http://${host}:${port}\nPress RETURN to stop...")
    system.registerOnTermination(() => bindingFuture.flatMap(_.unbind()))
    Await.result(system.whenTerminated, Duration.Inf)
  }


}
