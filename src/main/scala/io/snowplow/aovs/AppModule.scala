package io.snowplow.aovs

import com.google.inject.AbstractModule
import io.snowplow.aovs.api.ApiModule
import io.snowplow.aovs.dao.DaoModule
import io.snowplow.aovs.services.ServicesModule

class AppModule extends AbstractModule {
  override def configure(): Unit = {
    install(new ApiModule)
    install(new ServicesModule)
    install(new DaoModule)
  }
}
