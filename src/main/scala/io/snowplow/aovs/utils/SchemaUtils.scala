package io.snowplow.aovs.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.{JsonSchema, JsonSchemaFactory, SpecVersionDetector}

object SchemaUtils {
  def getSchemaFromString(schema: String, mapper: ObjectMapper): JsonSchema = {
    val jsonNode = mapper.readTree(schema)
    JsonSchemaFactory
      .getInstance(SpecVersionDetector.detect(jsonNode))
      .getSchema(jsonNode)
  }

}
