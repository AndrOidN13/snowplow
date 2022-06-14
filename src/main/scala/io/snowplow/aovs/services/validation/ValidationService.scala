package io.snowplow.aovs.services.validation

import io.snowplow.aovs.model.{FailureResponse, SuccessResponse}

import scala.concurrent.Future

trait ValidationService {
  def validate(schemaId: String, json: String): Future[Either[FailureResponse, SuccessResponse]]
}
