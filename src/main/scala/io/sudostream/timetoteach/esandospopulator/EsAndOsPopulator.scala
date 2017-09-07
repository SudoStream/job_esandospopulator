package io.sudostream.timetoteach.esandospopulator

import argonaut.Argonaut._
import argonaut._
//import io.sudostream.timetoteach.esandospopulator.EsAndOsPopulator.esAndOsCollection
import org.mongodb.scala._

object EsAndOsPopulator extends App with MongoDbHelper with ConsoleMessages {
  println(startupMessage)

  import scala.io.Source

  val filename = "/esAndOs.json"
  val filenameAsInputStream = getClass.getResourceAsStream(filename)
  val input = Source.fromInputStream(filenameAsInputStream).mkString


  // parse the string as json, attempt to decode it to a list of person,
  // otherwise just take it as an empty list.
  val esAndOs = input.decodeOption[List[EAndO]].getOrElse(Nil)

//  esAndOs.foreach(eAndO => println("EAndO\n-------\n" + eAndO + "\n"))

  // convert back to json, and then to a pretty printed string, alternative
  // ways to print may be nospaces, spaces2, or a custom format


  ///
  val esAndOsCollection = getEsAndOsCollection

  esAndOs.foreach {
    eo => {

      val esAndOsAsDocuments: List[Document] = eo.experienceAndOutcome.map {
        sentence =>
          Document("sentence" -> sentence.sentence,
            "bulletPoints" -> sentence.bulletPoints)
      }

      val eoDoc = Document(
        "experienceAndOutcome" -> esAndOsAsDocuments,
        "codes" -> eo.codes,
        "curriculumLevels" -> eo.curriculumLevels,
        "curriculumAreaName" -> eo.curriculumAreaName,
        "eAndOSetName" -> eo.eAndOSetName,
        "eAndOSetSectionName" -> eo.eAndOSetSectionName,
        "eAndOSetSubSectionName" -> eo.eAndOSetSubSectionName,
        "eAndOSetSubSectionAuxiliaryText" -> eo.eAndOSetSubSectionAuxiliaryText,
        "responsibilityOfAllPractitioners" -> eo.responsibilityOfAllPractitioners
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

  Thread.sleep(300000L)
  println(finishedMessage)
  System.exit(0)
}

trait ConsoleMessages {
  lazy val startupMessage = "EsAndOsPopulator starting..."
  lazy val finishedMessage = "EsAndOsPopulator done."
}

case class EAndO(experienceAndOutcome: List[Sentence],
                 codes: List[String],
                 curriculumLevels: List[String],
                 curriculumAreaName: String,
                 eAndOSetName: Option[String],
                 eAndOSetSectionName: Option[String],
                 eAndOSetSubSectionName: Option[String],
                 eAndOSetSubSectionAuxiliaryText: Option[String],
                 responsibilityOfAllPractitioners: Boolean
                )

object EAndO {
  implicit def EAndOCodecJson: CodecJson[EAndO] =
    casecodec9(EAndO.apply, EAndO.unapply)(
      "experienceAndOutcome",
      "codes",
      "curriculumLevels",
      "curriculumAreaName",
      "eAndOSetName",
      "eAndOSetSectionName",
      "eAndOSetSubSectionName",
      "eAndOSetSubSectionAuxiliaryText",
      "responsibilityOfAllPractitioners"
    )
}

case class Sentence(
                     sentence: String,
                     bulletPoints: List[String]
                   )

object Sentence {
  implicit def SentenceCodeJson: CodecJson[Sentence] =
    casecodec2(Sentence.apply, Sentence.unapply)(
      "sentence",
      "bulletPoints"
    )
}