package io.snowplow.aovs.services

import com.google.inject.AbstractModule
import io.snowplow.aovs.services.schema.{SchemaService, SchemaServiceImpl}
import io.snowplow.aovs.services.validation.{ValidationService, ValidationServiceImpl}

class ServicesModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[SchemaService]).to(classOf[SchemaServiceImpl])
    bind(classOf[ValidationService]).to(classOf[ValidationServiceImpl])
  }
}
