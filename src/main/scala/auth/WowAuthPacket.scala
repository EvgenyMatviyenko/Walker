package auth

import utils.ByteHelpers._

/**
  * Created by evgenymatviyenko on 11/13/17.
  */
class WowAuthPacket(content: WowAuthPacketContent) extends BytesRepresentable {
  def command: WowAuthCommand.Value = content match {
    case AuthLogonChallenge(_) => WowAuthCommand.AuthLogonChallange
  }

  override def asBytes: Array[Byte] = {
    val commandBytes = command.id.toBytes(1)
    val unknownBytes = 0x00.toBytes(1)
    val contentBytes = content.asBytes
    Array(commandBytes, unknownBytes, contentBytes).flatten
  }
}

sealed trait WowAuthPacketContent extends BytesRepresentable { }
case class AuthLogonChallenge(accountName: String) extends WowAuthPacketContent {
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