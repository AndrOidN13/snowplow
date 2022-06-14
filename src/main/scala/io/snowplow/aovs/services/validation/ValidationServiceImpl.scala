package io.snowplow.aovs.services.validation

import io.snowplow.aovs.model.{FailureResponse, SuccessResponse}
import io.snowplow.aovs.services.schema.SchemaService

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class ValidationServiceImpl @Inject()(schemaService: SchemaService) extends ValidationService {
  override def validate(schemaId: String, json: String): Future[Either[FailureResponse, SuccessResponse]] = ???
}
