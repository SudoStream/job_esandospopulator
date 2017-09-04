package io.sudostream.timetoteach.esandospopulator

import org.mongodb.scala._

object EsAndOsPopulator extends App with MongoDbHelper {
  println("EsAndOsPopulator starting...")

  val esAndOs = getEsAndOsCollection

  val doc: Document = Document("_id" -> 0, "name" -> "MongoDB", "type" -> "database",
    "count" -> 1, "info" -> Document("x" -> 203, "y" -> 102))

  val insertObservable = esAndOs.insertOne(doc)

  insertObservable.subscribe(
    new Observer[Completed] {
      override def onNext(result: Completed): Unit = println(s"onNext: $result")
      override def onError(e: Throwable): Unit = println(s"onError: $e")
      override def onComplete(): Unit = println("onComplete")
    }
  )

  val insertAndCount = for {
    insertResult <- insertObservable
    countResult <- esAndOs.count()
  } yield countResult

//  println(s"total # of documents after inserting 100 small ones (should be 101):  ${insertAndCount.subscribe()}")
  Thread.sleep(1000L)
  println("EsAndOsPopulator done.")
}

