package io.sudostream.timetoteach.esandospopulator

import java.util.concurrent.TimeUnit

import com.mongodb.ConnectionString
import com.typesafe.config.ConfigFactory
import org.mongodb.scala.connection.{ClusterSettings, NettyStreamFactoryFactory, SslSettings}
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
    println(s"mongo host = '$mongoDbHost'")
    println(s"mongo port = '$mongoDbPort'")


    val clusterSettings: ClusterSettings =
      if (!(mongoDbUri == "")) {
        ClusterSettings.builder()
          .applyConnectionString(new ConnectionString(mongoDbUri))
          .build()
      } else {
        ClusterSettings.builder().hosts(
          List(
            new ServerAddress(mongoDbHost, mongoDbPort))
            .asJava).build()
      }

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
