import Dependencies._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{dockerRepository, dockerUpdateLatest}

enablePlugins(JavaAppPackaging)
enablePlugins(UniversalPlugin)
enablePlugins(DockerPlugin)

name := "esandospopulator"
organization := "io.sudostream"
scalaVersion := "2.11.8"
version := "0.0.3"
dockerExposedPorts := Seq(9000)
dockerRepository := Some("eu.gcr.io/time-to-teach")
dockerUpdateLatest := true
packageName in Docker := "esandospopulator"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "io.netty" % "netty-all" % "4.1.15.Final",
  "io.argonaut" %% "argonaut" % "6.1",
  //      "org.reactivemongo" %% "reactivemongo" % "0.12.6",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  scalaTest % Test
)
