package auth.packets

import java.security.MessageDigest

import akka.util.ByteString
import auth.{BytesRepresentable, SRPProtocol, WowAuthCommand}
import utils.ByteHelpers._

import scala.util.hashing.Hashing

/**
  * Created by evgenymatviyenko on 11/15/17.
  */
class WowAuthClientPacket(content: WowAuthClientPacketContent) extends WowAuthPacket(content.command) with BytesRepresentable {
  def asBytes: ByteString = {
    val commandBytes = command.id.asBytes.reverse.take(1)
    val contentBytes = content.asBytes
    ByteString(Array(commandBytes, contentBytes).flatten)
  }

  override def toString: String = s"WowAuthClientPacket(content: $content)"
}

sealed trait WowAuthClientPacketContent extends BytesRepresentable {
  def command: WowAuthCommand.Value = this match {
    case AuthLogonChallenge(_) => WowAuthCommand.AuthLogonChallange
    case AuthLogonProof(_, _) => WowAuthCommand.AuthLogonProof
  }
}

case class AuthLogonChallenge(accountName: String) extends WowAuthClientPacketContent {
  override def asBytes: ByteString = {
    val gamename = "WoW".asBytes.reverse.take(3) ++ 0x00.asBytes.take(1)
    val version1 = 1.asBytes.reverse.take(1)
    val version2 = 12.asBytes.reverse.take(1)
    val version3 = 1.asBytes.reverse.take(1)
    val build = BigInt(5875).asBytes.reverse.take(2)
    val platform = 0x00.asBytes.take(1) ++ "x86".asBytes.reverse.take(3)
    val os = "OSX".asBytes.reverse.take(3) ++ 0x00.asBytes.take(1)
    val country = "enUS".asBytes.reverse.take(4)
    val timezoneBias = 120.asBytes.reverse.take(4)
    val ip = 0x00.asBytes.reverse.take(4)
    val i = accountName.asBytes
    val i_len = i.length.asBytes.reverse.take(1)

    val allBytes = Array(gamename, version1, version2, version3, build, platform, os, country, timezoneBias, ip, i_len, i).flatten
    val size = allBytes.length.asBytes.reverse.take(2)
    val error = 0x00.asBytes.reverse.take(1)

    ByteString(Array(error.toArray, size.toArray, allBytes).flatten)
  }

  override def toString: String = s"AuthLogonChallenge(accountName: $accountName)"
}

case class AuthLogonProof(bigA: BigInt, bigM: BigInt) extends  WowAuthClientPacketContent {
  override def asBytes: ByteString = {
    val bigABytes = bigA.asBytes
    val bigMBytes = bigM.asBytes
    val crcHashBytes = ByteString(Array.fill(20)(0x00.toByte))
    val keysCountBytes = 0x00.asBytes.reverse.take(1)
    val securityFlagsBytes = 0x00.asBytes.reverse.take(1)
    ByteString(Array(bigABytes, bigMBytes, crcHashBytes, keysCountBytes, securityFlagsBytes).flatten)
  }

  override def toString: String = s"AuthLogonProof(A: $bigA, M: $bigM)"
}