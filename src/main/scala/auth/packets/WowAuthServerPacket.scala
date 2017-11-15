package auth.packets

import akka.util.ByteString
import auth.WowAuthCommand
import utils.ByteHelpers._

/**
  * Created by evgenymatviyenko on 11/15/17.
  */
class WowAuthServerPacket(bytes: ByteString) extends WowAuthPacket(WowAuthCommand(bytes.head)) {
  val result = WowAuthResult(bytes(2))

  val content: WowAuthServerPacketContent = result match {
    case WowAuthSuccessResult(_) =>
      val contentBytes = bytes.drop(3)

      command match {
        case WowAuthCommand.AuthLogonChallange =>
          AuthLogonChallengeResponse(contentBytes)
        case _ =>
          AuthUnknownResponse()
      }
    case WowAuthFailureResult(code) =>
      AuthFailureResponse(code)
    case WowAuthUnknownResult() =>
      AuthUnknownResponse()
  }

  override def toString: String = s"WowAuthServerPacket(result: $result, content: $content)"
}

sealed trait WowAuthServerPacketContent { }
case class AuthUnknownResponse() extends WowAuthServerPacketContent { }
case class AuthFailureResponse(code: WowAuthFailureResultCode.Value) extends WowAuthServerPacketContent { }
case class AuthLogonChallengeResponse(bytes: ByteString) extends WowAuthServerPacketContent {
  val b = BigInt(bytes.take(32).toArray)
  val unknown1 = bytes.slice(32, 33)
  val g = bytes.slice(33, 34).head.toUnsignedInt
  val unknown2 = bytes.slice(34, 35)
  val n = BigInt(bytes.slice(35, 67).toArray)
  val s = BigInt(bytes.slice(67, 99).toArray)
  val unknown3 = bytes.slice(99, 115)
  val securityFlags = bytes.slice(115, 116).head.toUnsignedInt

  override def toString: String = s"AuthLogonChallengeResponse(b: $b, g: $g, n: $n, s: $s, securityFlags: $securityFlags)"
}