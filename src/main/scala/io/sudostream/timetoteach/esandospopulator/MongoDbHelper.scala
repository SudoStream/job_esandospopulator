package io.sudostream.timetoteach.esandospopulator

import java.util.concurrent.TimeUnit

import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait MongoDbHelper {

  def getEsAndOsCollection: MongoCollection[Document] = {
    val mongoClient: MongoClient = MongoClient("mongodb://localhost")
    val database: MongoDatabase = mongoClient.getDatabase("esandos")
    val dbDropObservable = database.drop()

    // We want to wait here until the database is dropped
    Await.result(dbDropObservable.toFuture, Duration(10, TimeUnit.SECONDS))

    val collection: MongoCollection[Document] = database.getCollection("esandos")
    collection
  }

}
