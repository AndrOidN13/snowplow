package io.snowplow.aovs.api

import akka.http.scaladsl.model.StatusCodes.{InternalServerError => AkkaInternalServerError, OK}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import io.snowplow.aovs.services.schema.SchemaService
import akka.http.scaladsl.marshalling.Marshaller._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.snowplow.aovs.model.{DownloadSchema, FailureResponse, InternalServerError, UploadSchema}
import io.snowplow.aovs.utils.AppLogger

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success}

@Singleton
class SchemaRoutes @Inject()(schemaService: SchemaService) extends AppLogger with FailFastCirceSupport {
  import io.circe.generic.auto._
  def routes: Route = path("schema" / Segment) { schemaInnerRoutes }

  private def schemaInnerRoutes(schemaId: String): Route = concat(
    post {
      entity(as[String])(schema => {
        onComplete(schemaService.saveSchema(schemaId, schema)) {
          case Success(either) => either.fold(
            failure => complete(failure.status.statusCode, failure),
            success => complete(success.status.statusCode, success)
          )
          case Failure(exception) =>
            logger.error("Unexpected error saving schema", exception)
            complete(AkkaInternalServerError, FailureResponse(UploadSchema, schemaId, InternalServerError, s"Unexpected server error: ${exception.getMessage}"))
        }
      })
    },
    get {
        onComplete(schemaService.getSchema(schemaId)) {
          case Success(either) => either.fold(
            failure => complete(failure.status.statusCode, failure),
            schema => complete(OK, schema)
          )
          case Failure(exception) =>
            logger.error("Unexpected error retrieving schema", exception)
            complete(AkkaInternalServerError, FailureResponse(DownloadSchema, schemaId, InternalServerError, s"Unexpected server error: ${exception.getMessage}"))
        }
    }
  )
}
