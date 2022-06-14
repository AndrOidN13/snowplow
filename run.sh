sbt clean assembly
java -cp target/scala-2.13/snowplow-assembly*.jar io.snowplow.aovs.App
