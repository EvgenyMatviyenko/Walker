package auth.packets

import akka.util.ByteString
import auth.WowAuthCommand

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
}

sealed trait WowAuthServerPacketContent { }
case class AuthUnknownResponse() extends WowAuthServerPacketContent { }
case class AuthFailureResponse(code: WowAuthFailureResultCode.Value) extends WowAuthServerPacketContent { }
case class AuthLogonChallengeResponse(bytes: ByteString) extends WowAuthServerPacketContent {
  val b = bytes.take(32)
  val unknown1 = bytes.slice(32, 33)
  val g = bytes.slice(33, 34)
  val unknown2 = bytes.slice(34, 35)
  val n = bytes.slice(35, 67)
  val s = bytes.slice(67, 99)
  val unknown3 = bytes.slice(99, 115)
  val securityFlags = bytes.slice(115, 116)
}