name := "amazon-reviews-analyzer"

version := "1.0"

scalaVersion := "2.12.2"

lazy val akkaVersion = "2.5.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "org.specs2" %% "specs2" % "2.4.17" % "test"
)

// JSON support
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.7.9"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.7.9"

// output to CSV file
libraryDependencies += "com.github.marklister" %% "product-collections" % "1.4.5"

// logging
//libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"
//libraryDependencies += "com.typesafe.scala-logging" % "scala-logging" % "3.1.0"

libraryDependencies += "org.scala-lang" % "scala-library" % "2.12.2" withSources()
libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.12.2" withSources()

// HttpClient for POST requests
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5"

// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies += "commons-io" % "commons-io" % "2.5"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

EclipseKeys.withSource := true