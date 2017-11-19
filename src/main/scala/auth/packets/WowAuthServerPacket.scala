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
      AuthEmptyResponse()
    case WowAuthUnknownResult() =>
      AuthUnknownResponse()
  }

  override def toString: String = s"WowAuthServerPacket(result: $result, content: $content)"
}

sealed trait WowAuthServerPacketContent { }
case class AuthUnknownResponse() extends WowAuthServerPacketContent { }
case class AuthEmptyResponse() extends WowAuthServerPacketContent { }
case class AuthLogonChallengeResponse(bytes: ByteString) extends WowAuthServerPacketContent {
  val bigB = bytes.take(32).reverse.toUnsignedBigInt
  //val unknown1 = bytes.slice(32, 33).reverse
  val g = bytes.slice(33, 34).reverse.toUnsignedBigInt
  //val unknown2 = bytes.slice(34, 35).reverse
  val bigN = bytes.slice(35, 67).reverse.toUnsignedBigInt
  val s = bytes.slice(67, 99).reverse.toUnsignedBigInt
  //val unknown3 = bytes.slice(99, 115).reverse
  val securityFlags = bytes.slice(115, 116).reverse.head.toUnsignedInt

  override def toString: String = s"AuthLogonChallengeResponse(N: $bigB, g: $g, N: $bigN, s: $s, securityFlags: $securityFlags)"
}