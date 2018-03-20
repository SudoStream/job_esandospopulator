package io.sudostream.timetoteach.esandospopulator

import argonaut.Argonaut._
import argonaut.CodecJson
import org.mongodb.scala.{Completed, Document, MongoCollection}

import scala.concurrent.Future
import scala.io.Source

trait SchoolTermInserter {

  def insertSchoolTermsToDatabase(schoolTerms: List[SchoolTerm],
                                  schoolsTermsCollection: MongoCollection[Document]): Future[Completed] = {
    val docsToInsert: Seq[Document] = schoolTerms.map {
      term =>
        Document(
          "localAuthority" -> term.localAuthority,
          "schoolYear" -> term.schoolYear,
          "schoolTermName" -> term.schoolTermName,
          "termFirstDay" -> term.termFirstDay,
          "termLastDay" -> term.termLastDay
        )
    }
    schoolsTermsCollection.insertMany(docsToInsert).toFuture
  }

  def decodeSchoolTermsForDatabaseInjestion: List[SchoolTerm] = {
    val filename = "/schoolTerms.json"
    val filenameAsInputStream = getClass.getResourceAsStream(filename)
    val input = Source.fromInputStream(filenameAsInputStream).mkString
    println("Lets decode the school terms ...")
    input.decodeOption[List[SchoolTerm]].getOrElse(Nil)
  }

}

case class SchoolTerm(
                       localAuthority: String,
                       schoolYear: String,
                       schoolTermName: String,
                       termFirstDay: String,
                       termLastDay: String
                     )

object SchoolTerm {
  implicit def SchoolTermCodecJson: CodecJson[SchoolTerm] =
    casecodec5(SchoolTerm.apply, SchoolTerm.unapply)(
      "localAuthority",
      "schoolYear",
      "schoolTermName",
      "termFirstDay",
      "termLastDay"
    )
}
