package io.snowplow.aovs.services.schema
import io.snowplow.aovs.dao.persistence.PersistenceDao
import io.snowplow.aovs.model._

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersionDetector
import io.snowplow.aovs.utils.AppLogger

import scala.util.Try

@Singleton
class SchemaServiceImpl @Inject()(persistenceDao: PersistenceDao) extends SchemaService with AppLogger {
  private val mapper = new ObjectMapper

  override def saveSchema(schemaId: String, schema: String): Future[Either[FailureResponse, SuccessResponse]] = {
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

  private[schema] def validateSchema(schemaId: String, schema: String): Either[FailureResponse, String] = {
    Try {
      val jsonNode = mapper.readTree(schema)
      JsonSchemaFactory
        .getInstance(SpecVersionDetector.detect(jsonNode))
        .getSchema(jsonNode)
        .toString
    }.fold(
      exc => {
        logger.error(s"Schema validation failed for schemaId $schemaId and schema $schema", exc)
        Left(FailureResponse(UploadSchema, schemaId, BadRequest, s"Schema validation failed: ${exc.getMessage}"))
      },
      Right(_)
    )
  }

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
