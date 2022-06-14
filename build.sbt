import sbtassembly.AssemblyKeys.assemblyMergeStrategy
import sbtassembly.AssemblyPlugin.autoImport.PathList
import sbtassembly.MergeStrategy

name := "snowplow"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies ++= Dependencies.libraries

ThisBuild / assemblyMergeStrategy := {
  case PathList(xs @ _*) if xs.last == "io.netty.versions.properties" => MergeStrategy.discard
  case PathList(xs @ _*) if xs.last == "module-info.class"  => MergeStrategy.discard
  case x                   => (ThisBuild / assemblyMergeStrategy).value.apply(x) // keep old strategy
}
assembly / test := {}
