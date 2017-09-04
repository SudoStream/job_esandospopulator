import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.sudostream",
//      scalaVersion := "2.12.3",
      scalaVersion := "2.11.8",
      version      := "0.0.1"
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
