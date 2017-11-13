package utils

import java.nio.ByteBuffer

/**
  * Created by evgenymatviyenko on 11/12/17.
  */
object ByteHelpers {
  implicit class IntWithBytes(x: Int) {
    def toBytes(length: Int) = {
      val intSize = 4
      val arr = ByteBuffer.allocate(intSize).putInt(x).array()
      arr.drop(intSize - length).reverse
    }
  }

  implicit class StringWithBytes(x: String) {
    def toBytes = x.toCharArray.flatMap(_.toBytes)
  }

  implicit class CharWithBytes(x: Char) {
    def toBytes = {
      val charSize = 2
      ByteBuffer.allocate(charSize).putChar(x).array().drop(1)
    }
  }
}
