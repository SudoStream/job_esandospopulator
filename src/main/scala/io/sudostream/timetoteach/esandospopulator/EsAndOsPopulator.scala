package io.sudostream.timetoteach.esandospopulator

import org.mongodb.scala._

import scala.concurrent.Future


object EsAndOsPopulator extends App
  with MongoDbHelper with EsAndOsInserter with ConsoleMessages {

  val mongoKeystorePassword = try {
    sys.env("MONGODB_KEYSTORE_PASSWORD")
  } catch {
    case e: Exception => ""
  }

  println("is it MINIKUBE?")

  val isMinikubeRun = try {
    if (sys.env("MINIKUBE_RUN") == "true") {
      println("MINIKUBE = yes")
      System.setProperty("javax.net.ssl.keyStore", "/etc/ssl/cacerts")
      System.setProperty("javax.net.ssl.keyStorePassword", mongoKeystorePassword)
      System.setProperty("javax.net.ssl.trustStore", "/etc/ssl/cacerts")
      System.setProperty("javax.net.ssl.trustStorePassword", mongoKeystorePassword)
    } else {
      println("MINIKUBE = no")
    }
  }
  catch {
    case e: Exception => ""
  }

  println(startupMessage)

  val esAndOs: List[EAndO] = decodeEsAndOsForDatabaseInjestion
  val esAndOsCollection: MongoCollection[Document] = getEsAndOsCollection
  val insertToDbFuture: Future[Completed] = insertEsAndOsToDatabase(esAndOs, esAndOsCollection)

  while (!insertToDbFuture.isCompleted) {
    println(s"Waiting for db inserts of E's & O's to complete...")
    Thread.currentThread().join(100L)
  }

  println(finishedMessage)
  System.exit(0)

}

trait ConsoleMessages {
  lazy val startupMessage = "EsAndOsPopulator starting..."
  lazy val finishedMessage = "EsAndOsPopulator done."
}

