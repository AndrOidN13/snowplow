package io.snowplow.aovs.api

import com.google.inject.AbstractModule

class ApiModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[SchemaRoutes])
    bind(classOf[ValidationRoutes])
  }
}
