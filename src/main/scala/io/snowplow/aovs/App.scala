package io.snowplow.aovs

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import com.google.inject.Guice
import com.typesafe.config.{Config, ConfigFactory}
import io.snowplow.aovs.api.{SchemaRoutes, ValidationRoutes}

object App {
  def main(args: Array[String]): Unit = {
    val config: Config = ConfigFactory.load()
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "JsonValidationServer")
    val schemaRoutes = Guice.createInjector(new AppModule).getInstance(classOf[SchemaRoutes])
    val validationRoutes = Guice.createInjector(new AppModule).getInstance(classOf[ValidationRoutes])
    Http()
      .newServerAt(config.getString("server.host"), config.getInt("server.port"))
      .bind(
        schemaRoutes.routes
          ~ validationRoutes.routes
      )
  }
}
