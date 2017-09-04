package io.sudostream.timetoteach.esandospopulator

import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}

trait MongoDbHelper {

  def getEsAndOsCollection: MongoCollection[Document] = {
    val mongoClient: MongoClient = MongoClient("mongodb://localhost")
    val database: MongoDatabase = mongoClient.getDatabase("esandos")
    val collection: MongoCollection[Document] = database.getCollection("esandos")
    collection
  }

}
