package io.sudostream.timetoteach.esandospopulator

import java.util.concurrent.TimeUnit

import com.mongodb.connection.ClusterSettings
import com.typesafe.config.ConfigFactory
import org.mongodb.scala.connection.{NettyStreamFactoryFactory, SslSettings}
import org.mongodb.scala.{Document, MongoClient, MongoClientSettings, MongoCollection, MongoDatabase, ServerAddress}

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait MongoDbHelper {

  def getEsAndOsCollection: MongoCollection[Document] = {
    val config = ConfigFactory.load()
    val mongoDbUri = config.getString("mongodb.connection_uri")
    val mongoDbHost = config.getString("mongodb.host")
    val mongoDbPort = config.getInt("mongodb.port")

    val mongoClient: MongoClient =
      if (!(mongoDbUri == "")) {
        println(s"mongo uri = '$mongoDbUri'")
        System.setProperty("org.mongodb.async.type", "netty")
        MongoClient(mongoDbUri)
      } else {
        println(s"mongo host = '$mongoDbHost'")
        println(s"mongo port = '$mongoDbPort'")

        val clusterSettings: ClusterSettings = ClusterSettings.builder().hosts(
          List(new ServerAddress(mongoDbHost, mongoDbPort)).asJava).build()

        val mongoSslClientSettings = MongoClientSettings.builder()
          .sslSettings(SslSettings.builder()
            .enabled(true)
            .build())
          .streamFactoryFactory(NettyStreamFactoryFactory())
          .clusterSettings(clusterSettings)
          .build()

        MongoClient(mongoSslClientSettings)
      }


    val database: MongoDatabase = mongoClient.getDatabase("esandos")

    val dbDropObservable = database.drop()
    // We want to wait here until the database is dropped
    Await.result(dbDropObservable.toFuture, Duration(10, TimeUnit.SECONDS))

    val collection: MongoCollection[Document] = database.getCollection("esandos")
    collection
  }

}
