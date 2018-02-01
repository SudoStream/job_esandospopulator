package io.sudostream.timetoteach.esandospopulator

import argonaut.Argonaut._
import argonaut._
import org.mongodb.scala.{Completed, Document, MongoCollection}

import scala.concurrent.Future
import scala.io.Source

trait EsAndOsInserter {

  def decodeEsAndOsForDatabaseInjestion: List[EAndO] = {
    val filename = "/esAndOs.json"
    val filenameAsInputStream = getClass.getResourceAsStream(filename)
    val input = Source.fromInputStream(filenameAsInputStream).mkString
    input.decodeOption[List[EAndO]].getOrElse(Nil)
  }

  def insertEsAndOsToDatabase(esAndOs: List[EAndO],
                              esAndOsCollection: MongoCollection[Document]): Future[Completed] = {
    val docsToInsert: Seq[Document] = esAndOs.map {
      eo =>
        val esAndOsAsDocuments: List[Document] = eo.experienceAndOutcome.map {
          sentence =>
            Document("sentence" -> sentence.sentence,
              "bulletPoints" -> sentence.bulletPoints)
        }

        Document(
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
    }
    esAndOsCollection.insertMany(docsToInsert).toFuture
  }


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