package io.sudostream.timetoteach.esandospopulator

import java.util.concurrent.TimeUnit

import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.typesafe.config.{Config, ConfigFactory}

trait MongoDbHelper {

  def getEsAndOsCollection: MongoCollection[Document] = {
    val config = ConfigFactory.load()
    val mongoConnectionUri = config.getString("mongodb.connection_uri")
    val mongoClient: MongoClient = MongoClient(mongoConnectionUri)
    val database: MongoDatabase = mongoClient.getDatabase("esandos")
    val dbDropObservable = database.drop()

    // We want to wait here until the database is dropped
    Await.result(dbDropObservable.toFuture, Duration(10, TimeUnit.SECONDS))

    val collection: MongoCollection[Document] = database.getCollection("esandos")
    collection
  }

}
