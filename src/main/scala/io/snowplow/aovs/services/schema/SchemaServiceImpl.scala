package io.snowplow.aovs.services.schema
import io.snowplow.aovs.dao.persistence.PersistenceDao
import io.snowplow.aovs.model.{DownloadSchema, FailureResponse, InternalServerError, NotFound, SuccessResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class SchemaServiceImpl @Inject()(persistenceDao: PersistenceDao) extends SchemaService {
  override def saveSchema(schemaId: String, schema: String): Future[Either[FailureResponse, SuccessResponse]] = ???
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
