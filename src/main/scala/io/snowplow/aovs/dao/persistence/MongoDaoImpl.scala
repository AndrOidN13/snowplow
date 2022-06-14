package io.snowplow.aovs.dao.persistence
import akka.Done
import io.snowplow.aovs.utils.{AppLogger, Retrying}

import javax.inject.Singleton
import scala.concurrent.Future

@Singleton
class MongoDaoImpl extends PersistenceDao with AppLogger with Retrying {
  override def saveSchema(schemaId: String, schema: String): Future[Done] = ???
  override def getSchema(schemaId: String): Future[Option[String]] = ???
}
