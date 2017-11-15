package auth

import akka.util.ByteString
import utils.ByteHelpers._

/**
  * Created by evgenymatviyenko on 11/13/17.
  */
abstract class WowAuthPacket(val command: WowAuthCommand.Value) { }

class WowAuthClientPacket(content: WowAuthClientPacketContent) extends WowAuthPacket(content.command) {
  def asBytes: Array[Byte] = {
    val commandBytes = command.id.toBytes(1)
    val unknownBytes = 0x00.toBytes(1)
    val contentBytes = content.asBytes
    Array(commandBytes, unknownBytes, contentBytes).flatten
  }
}

sealed trait WowAuthClientPacketContent extends BytesRepresentable {
  def command: WowAuthCommand.Value = this match {
    case AuthLogonChallenge(_) => WowAuthCommand.AuthLogonChallange
  }
}

case class AuthLogonChallenge(accountName: String) extends WowAuthClientPacketContent {
  override def asBytes: Array[Byte] = {
    val gamename = 0x00.toBytes(4)
    val version1 = 0x00.toBytes(1)
    val version2 = 0x00.toBytes(1)
    val version3 = 0x00.toBytes(1)
    val build = 0x00.toBytes(2)
    val platform = 0x00.toBytes(4)
    val os = 0x00.toBytes(4)
    val country = 0x00.toBytes(4)
    val timezoneBias = 0x00.toBytes(4)
    val ip = 0x00.toBytes(4)
    val i = accountName.toBytes
    val i_len = i.length.toBytes(1)
    val size = Array(gamename, version1, version2, version3, build, platform, os, country, timezoneBias, ip, i, i_len).flatten.length.toBytes(2)
    Array(size, gamename, version1, version2, version3, build, platform, os, country, timezoneBias, ip, i_len, i).flatten
  }
}

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