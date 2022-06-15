package io.snowplow.aovs.dao.persistence

import akka.Done
import com.typesafe.config.{Config, ConfigFactory}
import io.snowplow.aovs.utils.{AppLogger, Retrying}

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}
import java.util.concurrent.Executors
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._

@Singleton
class MongoDaoImpl extends PersistenceDao with AppLogger with Retrying {
  private val config: Config = ConfigFactory.load()
  private val mongoClient: MongoClient = MongoClient(
    s"mongodb://${config.getString("mongo.host")}:${config.getString("mongo.port")}/${config.getString("mongo.database")}"
  )
  private val database: MongoDatabase = mongoClient.getDatabase(config.getString("mongo.database"))
  private val schemasCollectionName = "schemas"

  //MongoDB driver documentation says that the API is asynchronous, but added a separate thread pool for IO just in case
  implicit private val ioExecutionContext = ExecutionContext.fromExecutor(
    Executors.newFixedThreadPool(config.getInt("mongo.threadPoolSize"))
  )

  override def saveSchema(schemaId: String, schema: String): Future[Done] = {
    retryAndLogError(errorMessage = "Saving schema to MongoDB failed", logger = logger)(
      database
      .getCollection(schemasCollectionName)
      .insertOne(Document(
        "_id" -> schemaId,
        "schema" -> schema
      ))
      .toFuture()
      .map(_ => Done)
    )
  }

  override def getSchema(schemaId: String): Future[Option[String]] = {
    retryAndLogError(errorMessage = "Schema retrieval from MongoDB failed", logger = logger)(
    database
      .getCollection(schemasCollectionName)
      .find(equal("_id", schemaId))
      .projection(fields(include("schema")))
      .first()
      .headOption()
      .map(_.map(_.getString("schema")))
    )
  }
}
