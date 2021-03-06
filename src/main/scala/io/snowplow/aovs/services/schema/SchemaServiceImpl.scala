package io.snowplow.aovs.services.schema
import io.snowplow.aovs.dao.persistence.PersistenceDao
import io.snowplow.aovs.model._

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.fasterxml.jackson.databind.ObjectMapper
import io.circe.Json
import io.snowplow.aovs.utils.{AppLogger, SchemaUtils}

import scala.util.Try

@Singleton
class SchemaServiceImpl @Inject()(persistenceDao: PersistenceDao) extends SchemaService with AppLogger {
  private val mapper = new ObjectMapper

  /**
   * Saves schema to database
   * @param schemaId id of a schema
   * @param schema JSON representation of a schema
   * @return either a [[SuccessResponse]] or a [[FailureResponse]]
   */
  override def saveSchema(schemaId: String, schema: Json): Future[Either[FailureResponse, SuccessResponse]] = {
    validateSchema(schemaId, schema)
      .map(s =>
        persistenceDao
          .saveSchema(schemaId, s)
          .map(_ => Right(SuccessResponse(UploadSchema, schemaId, Created)))
          .recover(exc => Left(FailureResponse(UploadSchema, schemaId, InternalServerError, exc.getMessage)))
      ).fold(
      failure => Future(Left(failure)),
      identity
    )
  }

  /**
   * Checks if a string is a valid JSON schema
   * @param schemaId id of the schema
   * @param schema JSON representation of a schema
   * @return either a validated schema string or a [[FailureResponse]]
   */
  private[schema] def validateSchema(schemaId: String, schema: Json): Either[FailureResponse, String] = {
    Try(SchemaUtils.getSchemaFromJson(schema, mapper).getSchemaNode.toString)
      .fold(
      exc => {
        logger.error(s"Schema validation failed for schemaId $schemaId and schema $schema", exc)
        Left(FailureResponse(UploadSchema, schemaId, BadRequest, s"Schema validation failed: ${exc.getMessage}"))
      },
      Right(_)
    )
  }

  /**
   * Gets schema from database
   * @param schemaId id of the schema to retrieve
   * @return either JSON string of a schema or a [[FailureResponse]]
   */
  override def getSchema(schemaId: String): Future[Either[FailureResponse, String]] = {
    persistenceDao
      .getSchema(schemaId)
      .map {
        case Some(schema) => Right(schema)
        case None => Left(FailureResponse(DownloadSchema, schemaId, NotFound, s"Schema with id $schemaId was not found in database"))
      }
      .recover(exc => Left(FailureResponse(DownloadSchema, schemaId, InternalServerError, exc.getMessage)))
  }
}
