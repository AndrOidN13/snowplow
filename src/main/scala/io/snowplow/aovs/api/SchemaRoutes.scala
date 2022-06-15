package io.snowplow.aovs.api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.model.StatusCodes.{OK, InternalServerError => AkkaInternalServerError}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.util.ByteString
import io.snowplow.aovs.services.schema.SchemaService
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Json
import io.snowplow.aovs.model.{DownloadSchema, FailureResponse, InternalServerError, UploadSchema}
import io.snowplow.aovs.utils.AppLogger

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success}

@Singleton
class SchemaRoutes @Inject()(schemaService: SchemaService) extends AppLogger with FailFastCirceSupport {
  def routes: Route = path("schema" / Segment) { schemaInnerRoutes }

  private def schemaInnerRoutes(schemaId: String): Route = concat(
    post {
      entity(as[Json])(schema => {
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
            schema => complete(OK, HttpEntity(ContentTypes.`application/json`, ByteString(schema)))
          )
          case Failure(exception) =>
            logger.error("Unexpected error retrieving schema", exception)
            complete(AkkaInternalServerError, FailureResponse(DownloadSchema, schemaId, InternalServerError, s"Unexpected server error: ${exception.getMessage}"))
        }
    }
  )
}
