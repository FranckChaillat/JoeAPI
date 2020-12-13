
lazy val root = (project in file("."))
  .settings(
    name := "joeAPI",
    version := "1.0",
    scalaVersion := "2.13.3",
    mainClass in Compile := Some("org.joe.api.Main")
  )

resolvers += "Maven Central Server" at "https://repo1.maven.org/maven2"

resolvers += "Typesafe Server" at "https://repo.typesafe.com/typesafe/releases"


libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.13.0",
  "io.circe" %% "circe-generic" % "0.13.0",
  "org.xerial" % "sqlite-jdbc" % "3.32.3.2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.26",
  "com.typesafe.akka" %% "akka-http"   % "10.1.11",
  "org.json4s" %% "json4s-jackson" % "3.6.7",
  "org.scalaz" %% "scalaz-core" % "7.2.28",
  "javax.xml.bind" % "jaxb-api" % "2.3.0"
)