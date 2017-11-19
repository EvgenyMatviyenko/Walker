package utils

import javax.xml.bind.DatatypeConverter

import akka.util.ByteString
import org.scalatest.{FlatSpec, Matchers}
import utils.ByteHelpers._

/**
  * Created by evgenymatviyenko on 11/18/17.
  */
class ByteHelpersSpec extends FlatSpec with Matchers {
  behavior of "byte string to unsigned big int convert"

  it should "correctly convert bytes to number" in {
    val testBytes = ByteString(DatatypeConverter.parseHexBinary("12340000"))
    assert(testBytes.toUnsignedBigInt == 305397760)
  }

  behavior of "int to bytes string"
}
