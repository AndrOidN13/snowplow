## JSON Validation API

API is implemented using `Akka HTTP`; `MongoDB` is used as persistent storage.

Used [json-schema-validator](https://github.com/networknt/json-schema-validator) for schema validation instead of the suggested library, 
because the former is better maintained and documented.
JSON parsing is done by [circe](https://circe.github.io/circe/), it also handles null values through the call to `deepDropNullValues` method.

I've added a couple of unit tests for the validation logic.
Tests can be run with `sbt test`

### Endpoints

#### Save schema

`POST /schema/SCHEMAID`

with JSON body containing schema.

Saves schema to database; returns either successful response
e.g. 
`{
"action": "uploadSchema",
"id": "test-schema-5",
"status": "success"
}`

or a failure, e.g.

`{
"action": "uploadSchema",
"id": "test-schema-6",
"status": "error",
"message": "Schema validation failed: Schema tag not present"
}`


#### Get schema

`GET /schema/SCHEMAID`

Returns schema as JSON in case of success, 

or a failure response, e.g.

`{
"action": "downloadSchema",
"id": "test-schema-22",
"status": "error",
"message": "Schema with id test-schema-22 was not found in database"
}`

#### Validate JSON document

`POST /validate/SCHEMAID`

with a JSON body containing document to be validated.

Validates JSON against given schema; returns either successful response
e.g.
`{
"action": "validateDocument",
"id": "test-schema-2",
"status": "success"
}`

or a failure, e.g.

`{
"action": "validateDocument",
"id": "test-schema-2",
"status": "error",
"message": "JSON schema validation failed with errors: $.destination: is missing but it is required"
}`

### How to run locally
To run application, `docker`, `sbt` and `java`(at least 8) are required.

The script `run.sh` pulls and runs MongoDB docker container, assembles the fat JAR and runs it.

After executing the script API should become available at `localhost:8080`.
