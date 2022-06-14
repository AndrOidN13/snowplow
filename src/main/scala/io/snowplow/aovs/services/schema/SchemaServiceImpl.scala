package io.snowplow.aovs.services.schema
import io.snowplow.aovs.dao.persistence.PersistenceDao
import io.snowplow.aovs.model.{FailureResponse, SuccessResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class SchemaServiceImpl @Inject()(persistenceDao: PersistenceDao) extends SchemaService {
  override def saveSchema(schemaId: String, schema: String): Future[Either[FailureResponse, SuccessResponse]] = ???
  override def getSchema(schemaId: String): Future[Either[FailureResponse, String]] = ???
}
