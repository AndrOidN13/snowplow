package io.snowplow.aovs.services.schema

import io.circe.syntax.EncoderOps
import org.scalatest.GivenWhenThen
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

import scala.io.Source
import io.circe.parser._

class SchemaServiceTest extends AnyFunSpecLike with Matchers with GivenWhenThen {
  describe("validateSchema") {
    it("valid schema should be successfully validated") {
      Given("a valid schema string")
      val validSchema = Source.fromResource("validSchema.json").mkString
      When("schema validation is attempted")
      val result = parse(validSchema).flatMap(jsonSchema => new SchemaServiceImpl(null).validateSchema("id", jsonSchema))
      Then("schema should be successfully validated")
      result.isRight shouldBe true
      result.map(_.trim).toOption.get shouldEqual validSchema.trim.replace("\n", "").replace(" ", "")
    }

    it("invalid schema should fail validation") {
      Given("an invalid schema string without schema tag")
      val validSchema = Source.fromResource("invalidSchemaNoTag.json").mkString
      When("schema validation is attempted")
      val result = parse(validSchema).flatMap(jsonSchema => new SchemaServiceImpl(null).validateSchema("id", jsonSchema))
      Then("schema should fail validation")
      result.isLeft shouldBe true
    }
  }
}
