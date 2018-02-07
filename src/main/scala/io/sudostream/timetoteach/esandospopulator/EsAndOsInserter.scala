package io.sudostream.timetoteach.esandospopulator

import argonaut.Argonaut._
import argonaut._
import org.mongodb.scala.{Completed, Document, MongoCollection}

import scala.concurrent.Future
import scala.io.Source

trait EsAndOsInserter {

  def decodeEsAndOsForDatabaseInjestion: List[EAndOsAtTheSubsectionLevel] = {
    val filename = "/esAndOs.json"
    val filenameAsInputStream = getClass.getResourceAsStream(filename)
    val input = Source.fromInputStream(filenameAsInputStream).mkString
    input.decodeOption[List[EAndOsAtTheSubsectionLevel]].getOrElse(Nil)
  }

  def insertEsAndOsToDatabase(eAndOsAtSubsectionLevelList: List[EAndOsAtTheSubsectionLevel],
                              esAndOsCollection: MongoCollection[Document]): Future[Completed] = {
    val docsToInsert: Seq[Document] = eAndOsAtSubsectionLevelList.map {
      eoAtSubsectionLevel =>
        val esAndOsAsDocuments: List[Document] = eoAtSubsectionLevel.allExperienceAndOutcomesAtTheSubSectionLevel.map {
              singleEAndO => Document(
                "code" -> singleEAndO.code,
                "sentences" -> singleEAndO.sentences.map{
                  sentence =>
                    Document("sentence" -> sentence.sentence,
                      "bulletPoints" -> sentence.bulletPoints)
                }
              )
        }

        Document(
          "allExperienceAndOutcomesAtTheSubSectionLevel" -> esAndOsAsDocuments,
          "associatedBenchmarks" -> eoAtSubsectionLevel.associatedBenchmarks,
          "curriculumLevel" -> eoAtSubsectionLevel.curriculumLevel,
          "curriculumAreaName" -> eoAtSubsectionLevel.curriculumAreaName,
          "eAndOSetSectionName" -> eoAtSubsectionLevel.eAndOSetSectionName,
          "eAndOSetSubSectionName" -> eoAtSubsectionLevel.eAndOSetSubSectionName,
          "eAndOSetSubSectionAuxiliaryText" -> eoAtSubsectionLevel.eAndOSetSubSectionAuxiliaryText,
          "responsibilityOfAllPractitioners" -> eoAtSubsectionLevel.responsibilityOfAllPractitioners
        )
    }
    esAndOsCollection.insertMany(docsToInsert).toFuture
  }


}

case class EAndOsAtTheSubsectionLevel(
                                       allExperienceAndOutcomesAtTheSubSectionLevel: List[EAndO],
                                       associatedBenchmarks: List[String],
                                       curriculumLevel: String,
                                       curriculumAreaName: String,
                                       eAndOSetSectionName: String,
                                       eAndOSetSubSectionName: Option[String],
                                       eAndOSetSubSectionAuxiliaryText: Option[String],
                                       responsibilityOfAllPractitioners: Boolean
                                     )

object EAndOsAtTheSubsectionLevel {
  implicit def EAndOsAtTheSubsectionLevelCodecJson: CodecJson[EAndOsAtTheSubsectionLevel] =
    casecodec8(EAndOsAtTheSubsectionLevel.apply, EAndOsAtTheSubsectionLevel.unapply)(
      "allExperienceAndOutcomesAtTheSubSectionLevel",
      "associatedBenchmarks",
      "curriculumLevel",
      "curriculumAreaName",
      "eAndOSetSectionName",
      "eAndOSetSubSectionName",
      "eAndOSetSubSectionAuxiliaryText",
      "responsibilityOfAllPractitioners"
    )
}

case class EAndO(
                  code: String,
                  sentences: List[Sentence]
                )
object EAndO {
  implicit def EandOCodecJson: CodecJson[EAndO] =
    casecodec2(EAndO.apply, EAndO.unapply)(
      "code",
      "sentences"
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