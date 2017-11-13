package auth

import akka.util.ByteString

/**
  * Created by evgenymatviyenko on 11/12/17.
  */
trait BytesRepresentable {
  def asBytes: Array[Byte]
}
