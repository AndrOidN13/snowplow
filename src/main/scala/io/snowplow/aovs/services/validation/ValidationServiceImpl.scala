package io.snowplow.aovs.services.validation

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.networknt.schema.JsonSchema
import io.circe.Json
import io.snowplow.aovs.model.{BadRequest, FailureResponse, Success, SuccessResponse, ValidateDocument}
import io.snowplow.aovs.services.schema.SchemaService
import io.snowplow.aovs.utils.{AppLogger, SchemaUtils}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters.SetHasAsScala

@Singleton
class ValidationServiceImpl @Inject()(schemaService: SchemaService) extends ValidationService with AppLogger {
  private val mapper = new ObjectMapper

  /**
   * Validates given JSON against a schema with given ID
   * @param schemaId ID of the schema
   * @param json JSON to validate
   * @return either a [[SuccessResponse]] or a [[FailureResponse]]
   */
  override def validate(schemaId: String, json: Json): Future[Either[FailureResponse, SuccessResponse]] = {
    schemaService
      .getSchema(schemaId)
      .map(schemaEither =>
        schemaEither.flatMap(sch =>
          validateWithSchema(
            schemaId,
            SchemaUtils.getSchemaFromString(sch, mapper),
            mapper.readTree(json.deepDropNullValues.toString())
          )
        )
      )
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
