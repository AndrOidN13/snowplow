docker run --name json-validation-mongo -d -p 27017:27017/tcp mongo:latest
sbt clean assembly
java -cp target/scala-2.13/snowplow-assembly*.jar io.snowplow.aovs.App
