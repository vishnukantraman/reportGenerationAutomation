name := "reportGernerationAutomation"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.4",
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8",
  "io.spray" %% "spray-json" % "1.3.5",
  "com.typesafe.akka" %% "akka-actor" % "2.5.25",
  "com.typesafe.akka" %% "akka-stream" % "2.5.25",
  "org.reactivemongo" %% "reactivemongo" % "0.20.3",
  "io.circe" %% "circe-core" % "0.12.3",
  "io.circe" %% "circe-generic" % "0.12.3",
  "io.circe" %% "circe-parser" % "0.12.3",
  "com.nrinaudo" %% "kantan.csv" % "0.6.0",
  "com.nrinaudo" %% "kantan.csv-generic" % "0.6.0",
  "com.github.blemale" %% "scaffeine" % "4.0.0"
)