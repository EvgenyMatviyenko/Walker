package utils

import javax.xml.bind.DatatypeConverter

import akka.util.ByteString
import org.scalatest.{FlatSpec, Matchers}
import utils.ByteHelpers._

/**
  * Created by evgenymatviyenko on 11/18/17.
  */
class ByteHelpersSpec extends FlatSpec {
  behavior of "byte string to unsigned big int convert"

  it should "correctly convert bytes to number" in {
    val testBytes = ByteString(DatatypeConverter.parseHexBinary("12340000"))
    assert(testBytes.toUnsignedBigInt == 305397760)
  }

  it should "round robin to same number" in {
    val number = BigInt(305397760)
    assert(number.asBytes.toUnsignedBigInt == number)
  }

  it should "round robin to same byte string" in {
    val testBytes = ByteString(DatatypeConverter.parseHexBinary("12340000"))
    assert(testBytes.toUnsignedBigInt.asBytes == testBytes)
  }
}
