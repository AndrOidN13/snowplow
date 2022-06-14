package io.snowplow.aovs.dao

import akka.Done
import scala.concurrent.Future

trait Dao {
  def saveSchema(schemaId: String, schema: String): Future[Done]
  def getSchema(schemaId: String): Future[Option[String]]
}
