package io.snowplow.aovs.services.validation

import com.fasterxml.jackson.databind.ObjectMapper
import io.circe.parser.parse
import io.snowplow.aovs.utils.SchemaUtils
import org.scalatest.GivenWhenThen
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class ValidationServiceTest extends AnyFunSpecLike with Matchers with GivenWhenThen {
  private val mapper = new ObjectMapper()
  describe("validateJsonWithSchema") {
    it("valid JSON should be successfully validated") {
      Given("a valid JSON string and a schema")
      val validJson = Source.fromResource("validJson.json").mkString
      val validSchema = Source.fromResource("validSchema.json").mkString
      When("validation is attempted")
      val result = new ValidationServiceImpl(null)
          .validateWithSchema(
            "id",
            SchemaUtils.getSchemaFromString(validSchema, mapper),
            mapper.readTree(validJson)
          )
      Then("JSON should be successfully validated")
      result.isRight shouldBe true
    }

    it("invalid JSON should fail validation") {
      Given("an invalid JSON string and a schema")
      val invalidJson = Source.fromResource("invalidJson.json").mkString
      val validSchema = Source.fromResource("validSchema.json").mkString
      When("validation is attempted")
      val result = new ValidationServiceImpl(null)
        .validateWithSchema(
          "id",
          SchemaUtils.getSchemaFromString(validSchema, mapper),
          mapper.readTree(invalidJson)
        )
      Then("JSON should fail validation")
      result.isLeft shouldBe true
    }
  }
}
