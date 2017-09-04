package io.sudostream.timetoteach.esandospopulator

import argonaut.Argonaut._
import argonaut._
import org.mongodb.scala._

object EsAndOsPopulator extends App with MongoDbHelper with ConsoleMessages {
  println(startupMessage)

  import scala.io.Source

  val filename = "/esAndOs.json"
  val filenameAsInputStream = getClass.getResourceAsStream(filename)
  println("filenameAsInputStream: " + filenameAsInputStream)
  val input = Source.fromInputStream(filenameAsInputStream).mkString

  //  val esAndOsTextBuilder = new StringBuilder
  //  for (line <- Source.fromResource (filename).getLines) {
  //    esAndOsTextBuilder.append(line)
  //  }
  //  val input = esAndOsTextBuilder.toString()


  // parse the string as json, attempt to decode it to a list of person,
  // otherwise just take it as an empty list.
  val esAndOs = input.decodeOption[List[EAndO]].getOrElse(Nil)

  // work with your data types as you normally would
  esAndOs.foreach(eAndO => println("EAndO\n-------\n" + eAndO + "\n"))

  // convert back to json, and then to a pretty printed string, alternative
  // ways to print may be nospaces, spaces2, or a custom format


  ///
  val esAndOsCollection = getEsAndOsCollection

  esAndOs.foreach {
    eo => {
      val eoDoc = Document(
        "_id" -> eo.experienceAndOutcome,
        "codes" -> eo.codes,
        "curriculumLevels" -> eo.curriculumLevels,
        "curriculumAreaName" -> eo.curriculumAreaName,
        "eAndOSetName" -> eo.eAndOSetName,
        "eAndOSetSectionName" -> eo.eAndOSetSectionName,
        "eAndOSetSubSectionName" -> eo.eAndOSetSubSectionName
      )
      val insertObservable = esAndOsCollection.insertOne(eoDoc)

      insertObservable.subscribe(
        new Observer[Completed] {
          override def onNext(result: Completed): Unit = println(s"onNext: $result")
          override def onError(e: Throwable): Unit = println(s"onError: $e")
          override def onComplete(): Unit = println("onComplete")
        }
      )
    }
  }


  //  val doc: Document = Document("_id" -> 0, "name" -> "MongoDB", "type" -> "database",
  //    "count" -> 1, "info" -> Document("x" -> 203, "y" -> 102))

  //
  //  val insertAndCount = for {
  //    insertResult <- insertObservable
  //    countResult <- esAndOs.count()
  //  } yield countResult
  //  println(s"total # of documents after inserting 100 small ones (should be 101):  ${insertAndCount.subscribe()}")
  Thread.sleep(1000L)
  println(finishedMessage)
}

trait ConsoleMessages {
  lazy val startupMessage = "EsAndOsPopulator starting..."
  lazy val finishedMessage = "EsAndOsPopulator done."
}

case class EAndO(experienceAndOutcome: String,
                 codes: List[String],
                 curriculumLevels: List[String],
                 curriculumAreaName: String,
                 eAndOSetName: Option[String],
                 eAndOSetSectionName: Option[String],
                 eAndOSetSubSectionName: Option[String]
                )

object EAndO {
  implicit def PersonCodecJson: CodecJson[EAndO] =
    casecodec7(EAndO.apply, EAndO.unapply)(
      "experienceAndOutcome",
      "codes",
      "curriculumLevels",
      "curriculumAreaName",
      "eAndOSetName",
      "eAndOSetSectionName",
      "eAndOSetSubSectionName"
    )
}