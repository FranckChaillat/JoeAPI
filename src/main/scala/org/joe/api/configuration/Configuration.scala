package org.joe.api.configuration

import com.typesafe.config.Config

final case class Configuration(connectionString : String, host: String, port: Int)

object Configuration {
  def getConfiguration()(implicit config: Config): Configuration = {
    val connStr = config.getString("org.joe.api.repository.sqlite.connectionString")
    Configuration(connStr, config.getString("org.joe.api.http.host"), config.getInt("org.joe.api.http.port"))
  }
}