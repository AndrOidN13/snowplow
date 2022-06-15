package io.snowplow.aovs.services.schema

import io.circe.Json
import io.snowplow.aovs.model.{FailureResponse, SuccessResponse}

import scala.concurrent.Future

trait SchemaService {
  def saveSchema(schemaId: String, schema: Json): Future[Either[FailureResponse, SuccessResponse]]
  def getSchema(schemaId: String): Future[Either[FailureResponse, String]]
}
