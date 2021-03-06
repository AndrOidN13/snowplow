import sbt._

object Dependencies {
  val versions = new {
    val compile = new {
      val akka: String = "2.6.8"
      val akkaHttp: String = "10.2.9"
      val guice: String = "5.1.0"
      val slf4j: String = "1.7.36"
      val circe: String = "0.14.2"
      val akkaHttpCirce: String = "1.39.2"
      val mongoScalaDriver: String = "2.9.0"
      val jsonSchemaValidator: String = "1.0.70"
    }
    val test = new {
      val scalaTest: String = "3.2.12"
    }
  }

  val libraries : Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-http" % versions.compile.akkaHttp,
    "com.typesafe.akka" %% "akka-actor-typed" % versions.compile.akka,
    "com.typesafe.akka" %% "akka-stream" % versions.compile.akka,
    "com.google.inject" % "guice" % versions.compile.guice,
    "org.slf4j" % "slf4j-api" % versions.compile.slf4j,
    "org.slf4j" % "slf4j-simple" % versions.compile.slf4j,
    "org.slf4j" % "slf4j-simple" % versions.compile.slf4j,
    "io.circe" %% "circe-core" % versions.compile.circe,
    "io.circe" %% "circe-generic" % versions.compile.circe,
    "io.circe" %% "circe-parser" % versions.compile.circe,
    "de.heikoseeberger" %% "akka-http-circe" % versions.compile.akkaHttpCirce,
    "org.mongodb.scala" %% "mongo-scala-driver" % versions.compile.mongoScalaDriver,
    "com.networknt" % "json-schema-validator" % versions.compile.jsonSchemaValidator,
    "org.scalatest" %% "scalatest" % versions.test.scalaTest % Test
  )
}
