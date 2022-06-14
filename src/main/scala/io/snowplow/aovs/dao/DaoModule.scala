package io.snowplow.aovs.dao

import com.google.inject.AbstractModule
import io.snowplow.aovs.dao.persistence.{PersistenceDao, MongoDaoImpl}

class DaoModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PersistenceDao]).to(classOf[MongoDaoImpl])
  }
}
