package io.snowplow.aovs.api

import akka.http.scaladsl.model.StatusCodes.{InternalServerError => AkkaInternalServerError}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Json
import io.snowplow.aovs.model.{FailureResponse, InternalServerError, ValidateDocument}
import io.snowplow.aovs.services.validation.ValidationService
import io.snowplow.aovs.utils.AppLogger

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success}

@Singleton
class ValidationRoutes @Inject()(validationService: ValidationService) extends AppLogger with FailFastCirceSupport {
  import io.circe.generic.auto._
  def routes: Route = path("validate" / Segment) { schemaId =>
    post {
    entity(as[Json])(json => {
      onComplete(validationService.validate(schemaId, json)) {
        case Success(either) => either.fold(
          failure => complete(failure.status.statusCode, failure),
          success => complete(success.status.statusCode, success)
        )
        case Failure(exception) =>
          logger.error("Unexpected error validating document", exception)
          complete(AkkaInternalServerError, FailureResponse(ValidateDocument, schemaId, InternalServerError, s"Unexpected server error: ${exception.getMessage}"))
      }
    })
  }}
}
