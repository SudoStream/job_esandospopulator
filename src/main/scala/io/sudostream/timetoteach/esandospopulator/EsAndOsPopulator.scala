package io.sudostream.timetoteach.esandospopulator

import org.mongodb.scala._

import scala.concurrent.Future

object EsAndOsPopulator extends App
  with MongoDbHelper with EsAndOsInserter with ConsoleMessages {

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

