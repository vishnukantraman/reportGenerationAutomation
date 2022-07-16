import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerAlias
name := "reportGernerationAutomation"

version := "0.1"

scalaVersion := "2.13.1"


dockerExposedVolumes := Seq("/opt/docker/data")
dockerCommands ++= Seq(
//  ExecCmd("RUN", "mkdir", "/opt/docker/data"),
  ExecCmd("RUN", "chmod", "u+x", "/opt/docker/data")
)

val dockerImage = "reportgernerationautomation"
val dockerRepo = sys.env.getOrElse("REGISTRY_URL", "localhost:5001")

// customizations for the sbt-dynver and docker outputs
dynverSeparator in ThisBuild := "-"
//dynverVTagPrefix in ThisBuild := true
//dynverTagPrefix in ThisBuild := "v"

// a fallback in case something goes terribly wrong with the searchTagFmt
def fallbackVersion(d: java.util.Date): String = s"HEAD-${sbtdynver.DynVer timestamp d}"

// the tag formatting functions for the image tag we will use to check the existence of an image in the registry for a given commit
def searchTagFmt(out: sbtdynver.GitDescribeOutput): String = {
  "commit-" + out.commitSuffix.sha
}

lazy val root = project.in(file(".")).
  settings(
    inThisBuild(List(
      organization    := "net.rvk",
      scalaVersion    := "2.13.1"
    )),
    name := "AkkaHttpServer",
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
      "com.github.blemale" %% "scaffeine" % "4.0.0",
      "com.typesafe.akka" %% "akka-slf4j" % "2.5.25",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    ),
    maintainer := "vishnukantraman",
    dockerBaseImage := "openjdk:jre-alpine",
    dockerExposedPorts := Seq(8080, 27017),
    mainClass in Compile := Some("net.rvk.reportgeneration.services.AkkaHttp"),
    dockerRepository := Some(dockerRepo),
    packageName in Docker := dockerImage,
    dockerAlias := com.typesafe.sbt.packager.docker.DockerAlias(dockerRepository.value, None, (packageName in Docker).value, Some(dynverGitDescribeOutput.value.mkVersion(searchTagFmt, fallbackVersion(dynverCurrentDate.value))))
  ).
  enablePlugins(JavaAppPackaging).
  enablePlugins(DockerPlugin).
  enablePlugins(AshScriptPlugin)