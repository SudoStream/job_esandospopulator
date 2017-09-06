package io.sudostream.timetoteach.esandospopulator

import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory
import org.mongodb.scala.{Document, MongoClient, MongoClientSettings, MongoCollection, MongoDatabase, ServerAddress}

import scala.collection.JavaConverters._
import org.mongodb.scala.connection.ClusterSettings

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.mongodb.scala.connection.{NettyStreamFactoryFactory, SslSettings}

trait MongoDbHelper {

  def getEsAndOsCollection: MongoCollection[Document] = {
    val config = ConfigFactory.load()
    val mongoConnectionUri = config.getString("mongodb.connection_uri")

    val clusterSettings: ClusterSettings = ClusterSettings.builder().hosts(List(new ServerAddress(mongoConnectionUri)).asJava).build()

    val mongoSslClientSettings = MongoClientSettings.builder()
      .sslSettings(SslSettings.builder()
        .enabled(true)
        .build())
      .streamFactoryFactory(NettyStreamFactoryFactory())
        .clusterSettings(clusterSettings)
      .build()

    val mongoClient: MongoClient = MongoClient(mongoSslClientSettings)
    val database: MongoDatabase = mongoClient.getDatabase("esandos")
    val dbDropObservable = database.drop()

    // We want to wait here until the database is dropped
    Await.result(dbDropObservable.toFuture, Duration(10, TimeUnit.SECONDS))

    val collection: MongoCollection[Document] = database.getCollection("esandos")
    collection
  }

}
