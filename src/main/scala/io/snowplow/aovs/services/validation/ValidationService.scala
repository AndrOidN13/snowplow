package io.snowplow.aovs.services.validation

import io.circe.Json
import io.snowplow.aovs.model.{FailureResponse, SuccessResponse}

import scala.concurrent.Future

trait ValidationService {
  def validate(schemaId: String, json: Json): Future[Either[FailureResponse, SuccessResponse]]
}
