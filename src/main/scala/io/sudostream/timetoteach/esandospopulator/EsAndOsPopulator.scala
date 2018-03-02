package io.sudostream.timetoteach.esandospopulator

import org.mongodb.scala._

import scala.concurrent.Future


object EsAndOsPopulator extends App
  with MongoDbHelper with EsAndOsInserter with BenchmarksInserter with ConsoleMessages {

  val mongoKeystorePassword = try {
    sys.env("MONGODB_KEYSTORE_PASSWORD")
  } catch {
    case e: Exception => ""
  }

  println("is it MINIKUBE????????????????????")

  val isMinikubeRun  :Boolean = try {
    if (sys.env("MINIKUBE_RUN") == "true") {
      println("MINIKUBE = yes")
      System.setProperty("javax.net.ssl.keyStore", "/etc/ssl/cacerts")
      System.setProperty("javax.net.ssl.keyStorePassword", mongoKeystorePassword)
      System.setProperty("javax.net.ssl.trustStore", "/etc/ssl/cacerts")
      System.setProperty("javax.net.ssl.trustStorePassword", mongoKeystorePassword)
      true
    } else {
      println("MINIKUBE = no")
      false
    }
  }
  catch {
    case e: Exception => ""
      false
  }

  println(startupMessage)
  println("Time for some decoding ...")
  val esAndOs: List[EAndOsAtTheSubsectionLevel] = decodeEsAndOsForDatabaseInjestion
  println(s"The list of es and os has ${decodeEsAndOsForDatabaseInjestion.size} elems")
  val esAndOsCollection: MongoCollection[Document] = getEsAndOsCollection

  val benchmarks : List[Benchmark] = decodeBenchmarksForDatabaseInjestion
  val benchmarksCollection: MongoCollection[Document] = getBenchmarksCollection

  val insertToDbFuture: Future[Completed] = insertEsAndOsToDatabase(esAndOs, esAndOsCollection)
//  val insertBenchmarksToDbFuture: Future[Completed] = insertBenchmarksToDatabase(benchmarks, benchmarksCollection)

  while (!insertToDbFuture.isCompleted) {
    println(s"Waiting for db inserts of E's & O's to complete...")
    Thread.currentThread().join(100L)
  }

//  while (!insertBenchmarksToDbFuture.isCompleted) {
//    println(s"Waiting for db inserts of Benchmarks to complete...")
//    Thread.currentThread().join(100L)
//  }

  println(finishedMessage)
  System.exit(0)

}

trait ConsoleMessages {
  lazy val startupMessage = "EsAndOsPopulator starting..."
  lazy val finishedMessage = "EsAndOsPopulator done."
}

