package io.sudostream.timetoteach.esandospopulator

import argonaut.Argonaut._
import argonaut.CodecJson
import org.mongodb.scala.{Completed, Document, MongoCollection}

import scala.concurrent.Future
import scala.io.Source

trait BenchmarksInserter {
  def decodeBenchmarksForDatabaseInjestion: List[Benchmark] = {
    val filename = "/benchmarks.json"
    val filenameAsInputStream = getClass.getResourceAsStream(filename)
    val input = Source.fromInputStream(filenameAsInputStream).mkString
    input.decodeOption[List[Benchmark]].getOrElse(Nil)
    input.decodeOption[List[Benchmark]].getOrElse(Nil)
  }

  def insertBenchmarksToDatabase(benchmarks: List[Benchmark],
                                 benchmarksCollection: MongoCollection[Document]): Future[Completed] = {
    println(s"We have ${benchmarks.size} benchmarks to insert")

    val docsToInsert: Seq[Document] = benchmarks.map {
      benchmark =>
        Document(
          "eandoCodes" -> benchmark.eandoCodes,
          "level" -> benchmark.level,
          "benchmarks" -> benchmark.benchmarks
        )
    }
    benchmarksCollection.insertMany(docsToInsert).toFuture
  }

}

case class Benchmark(eandoCodes: List[String],
                     level: String,
                     benchmarks: List[String]
                    )

object Benchmark {
  implicit def BenchmarkCodecJson: CodecJson[Benchmark] =
    casecodec3(Benchmark.apply, Benchmark.unapply)(
      "eandoCodes",
      "level",
      "benchmarks"
    )
}
