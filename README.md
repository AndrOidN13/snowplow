## JSON Validation API

API is implemented using `Akka HTTP`; `MongoDB` is used as persistent storage.

### Endpoints

### How to run locally
To run application, `docker`, `sbt` and `java`(at least 8) are required.

The script `run.sh` pulls and runs MongoDB docker container, assembles the fat JAR and runs it.

After executing the script API should become available at `localhost:8080`.
