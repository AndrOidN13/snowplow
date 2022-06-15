package io.snowplow.aovs.services.validation

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.networknt.schema.JsonSchema
import io.snowplow.aovs.model.{BadRequest, FailureResponse, Success, SuccessResponse, ValidateDocument}
import io.snowplow.aovs.services.schema.SchemaService
import io.snowplow.aovs.utils.{AppLogger, SchemaUtils}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters.SetHasAsScala
import scala.util.Try

@Singleton
class ValidationServiceImpl @Inject()(schemaService: SchemaService) extends ValidationService with AppLogger {
  private val mapper = new ObjectMapper

  override def validate(schemaId: String, json: String): Future[Either[FailureResponse, SuccessResponse]] = {
    isValidJson(json, schemaId)
      .map(cleanupNulls)
      .map(j =>
        schemaService
          .getSchema(schemaId)
          .map(schemaEither =>
            schemaEither.flatMap(sch =>
              validateWithSchema(schemaId, SchemaUtils.getSchemaFromString(sch, mapper), j)
            )
          )
      ).fold(
      failure => Future(Left(failure)),
      identity
    )
  }

  private[validation] def isValidJson(maybeJson: String, schemaId: String): Either[FailureResponse, JsonNode] = {
    Try(mapper.readTree(maybeJson))
      .fold(
        exc => {
          logger.error(s"Input string is not a valid JSON: $maybeJson", exc)
          Left(FailureResponse(ValidateDocument, schemaId, BadRequest, s"Input string $maybeJson is not a valid JSON: ${exc.getMessage}"))
        },
        Right(_)
      )
  }

  private[validation] def cleanupNulls(json: JsonNode): JsonNode = {
    val it = json.iterator
    while (it.hasNext) {
      val child = it.next
      if (child.isNull) it.remove()
      else cleanupNulls(child)
    }
    json
  }

  private[validation] def validateWithSchema(schemaId: String, schema: JsonSchema, json: JsonNode): Either[FailureResponse, SuccessResponse] = {
    val validationResult = schema.validate(json)
    if(validationResult.isEmpty) Right(SuccessResponse(ValidateDocument, schemaId, Success))
    else {
      val errorMessage = validationResult.asScala.map(_.toString).mkString("JSON schema validation failed with errors: ", ";", "")
      Left(FailureResponse(ValidateDocument, schemaId, BadRequest, errorMessage))
    }
  }
}
