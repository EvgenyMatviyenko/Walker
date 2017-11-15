package auth.packets

import akka.util.ByteString
import auth.{BytesRepresentable, WowAuthCommand}
import utils.ByteHelpers._

/**
  * Created by evgenymatviyenko on 11/15/17.
  */
class WowAuthClientPacket(content: WowAuthClientPacketContent) extends WowAuthPacket(content.command) with BytesRepresentable {
  def asBytes: ByteString = {
    val commandBytes = command.id.asBytes.take(1)
    val contentBytes = content.asBytes
    ByteString(Array(commandBytes, contentBytes).flatten)
  }

  override def toString: String = s"WowAuthClientPacket(content: $content)"
}

sealed trait WowAuthClientPacketContent extends BytesRepresentable {
  def command: WowAuthCommand.Value = this match {
    case AuthLogonChallenge(_) => WowAuthCommand.AuthLogonChallange
    case AuthLogonProof() => WowAuthCommand.AuthLogonProof
  }
}

case class AuthLogonChallenge(accountName: String) extends WowAuthClientPacketContent {
  override def asBytes: ByteString = {
    val gamename = 0x00.asBytes.take(4)
    val version1 = 0x00.asBytes.take(1)
    val version2 = 0x00.asBytes.take(1)
    val version3 = 0x00.asBytes.take(1)
    val build = 0x00.asBytes.take(2)
    val platform = 0x00.asBytes.take(4)
    val os = 0x00.asBytes.take(4)
    val country = 0x00.asBytes.take(4)
    val timezoneBias = 0x00.asBytes.take(4)
    val ip = 0x00.asBytes.take(4)
    val i = accountName.asBytes
    val i_len = i.length.asBytes.take(1)

    val allBytes = Array(gamename, version1, version2, version3, build, platform, os, country, timezoneBias, ip, i_len, i).flatten
    val size = allBytes.length.asBytes.take(2)
    val error = 0x00.asBytes.take(1)

    ByteString(Array(error.toArray, size.toArray, allBytes).flatten)
  }

  override def toString: String = s"AuthLogonChallenge(accountName: $accountName)"
}

case class AuthLogonProof() extends  WowAuthClientPacketContent {
  override def asBytes: ByteString = {
    val a = 0x00.asBytes.take(32)
    val m1 = 0x00.asBytes.take(20)
    val crcHash = 0x00.asBytes.take(20)
    val keysCount = 0x00.asBytes.take(1)
    val securityFlags = 0x00.asBytes.take(1)
    ByteString(Array(a, m1, crcHash, keysCount, securityFlags).flatten)
  }

  override def toString: String = s"AuthLogonProof()"
}