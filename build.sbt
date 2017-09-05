import Dependencies._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{dockerRepository, dockerUpdateLatest}

enablePlugins(JavaAppPackaging)
enablePlugins(UniversalPlugin)
enablePlugins(DockerPlugin)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.sudostream",
      scalaVersion := "2.11.8",
      version := "0.0.1",
      dockerExposedPorts := Seq(9000),
      dockerRepository := Some("eu.gcr.io/job-esandospopulator"),
      dockerUpdateLatest := true,
      //version in Docker := version.value + "-" + java.util.UUID.randomUUID.toString
      packageName in Docker := "job-esandospopulator/api"
    )),
    name := "EsAndOsPopulator",
    libraryDependencies ++= Seq(
      "io.argonaut" %% "argonaut" % "6.1",
      //      "org.reactivemongo" %% "reactivemongo" % "0.12.6",
      "org.mongodb.scala" %% "mongo-scala-driver" % "2.1.0",
      "ch.qos.logback" % "logback-classic" % "1.1.7",
      scalaTest % Test
    )
  )
