package utils

import java.nio.ByteBuffer

import akka.util.ByteString
import auth.BytesRepresentable

/**
  * Created by evgenymatviyenko on 11/12/17.
  */
object ByteHelpers {
  implicit class IntWithBytes(x: Int) extends BytesRepresentable {
    override def asBytes: ByteString = ByteString(ByteBuffer.allocate(4).putInt(x).array().reverse)
  }

  implicit class StringWithBytes(x: String) extends BytesRepresentable {
    override def asBytes: ByteString = ByteString(x.toCharArray.flatMap(_.toBytes))
  }

  implicit class CharWithBytes(x: Char) {
    def toBytes = ByteString(ByteBuffer.allocate(2).putChar(x).array().reverse.take(1))
  }

  implicit class ByteWithUnsignedInt(x: Byte) {
    def toUnsignedInt: Int = java.lang.Byte.toUnsignedInt(x)
  }
}
