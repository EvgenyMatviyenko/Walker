package utils

import java.nio.ByteBuffer

import akka.util.ByteString
import auth.BytesRepresentable

/**
  * Created by evgenymatviyenko on 11/12/17.
  */
object ByteHelpers {
  implicit class IntBytesRepresentable(x: Int) extends BytesRepresentable {
    override def asBytes: ByteString = ByteString(ByteBuffer.allocate(4).putInt(x).array())
  }

  implicit class StringBytesRepresentable(x: String) extends BytesRepresentable {
    override def asBytes: ByteString = ByteString(x.toCharArray.flatMap(_.toBytes))
  }

  implicit class BigIntBytesRepresentable(x: BigInt) extends BytesRepresentable {
    override def asBytes: ByteString = ByteString(x.toByteArray)
  }

  implicit class CharBytesRepresentable(x: Char) {
    def toBytes = ByteString(ByteBuffer.allocate(2).putChar(x).array().drop(1))
  }

  implicit class ByteToUnsignedInt(x: Byte) {
    def toUnsignedInt: Int = java.lang.Byte.toUnsignedInt(x)
  }

  implicit class ByteStringToUnsignedBigInt(x: ByteString) {
    def toUnsignedBigInt = BigInt(Array(Array(0x00.toByte), x.toArray).flatten)
  }
}
