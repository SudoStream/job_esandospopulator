package io.sudostream.timetoteach.esandospopulator

import org.scalatest._

class EsAndOsPopulatorSpec extends FlatSpec with Matchers {
  "The Hello object" should "say hello" in {
    EsAndOsPopulator.greeting shouldEqual "hello"
  }
}
