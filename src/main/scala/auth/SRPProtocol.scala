package auth

import java.security.MessageDigest

import akka.util.ByteString
import scala.util.Random

import utils.ByteHelpers._

/**
  * Created by evgenymatviyenko on 11/17/17.
  */
object SRPProtocol {
  def calculate(bigN: BigInt, g: BigInt, bigB: BigInt, s: BigInt, identity: String, password: String, random: Random): (BigInt, BigInt) = {
    val k = hash(bigN, g).toUnsignedBigInt
    val a = cryptrand(1024, bigN, random)
    val bigA = g.modPow(a, bigN)
    val u = hash(bigA, bigB).toUnsignedBigInt
    val x = hash(s, hash((identity ++ ":" ++ password).asBytes)).toUnsignedBigInt
    //val bigS = (bigB - g.modPow(x, bigN)).modPow(a + u * x, bigN)
    val bigS = (bigB - k * g.modPow(x, bigN)).modPow(a + u * x, bigN)
    val bigK = hashInterleaved(bigS)
    val bigM = hash(hash(bigN).toUnsignedBigInt ^ hash(g).toUnsignedBigInt, hash(identity.asBytes), s, bigA, bigB, bigK).toUnsignedBigInt
    (bigA, bigM)
  }

  private def cryptrand(n: Int, bigN: BigInt, random: Random) = BigInt(n, random) % bigN

  def token(identity: String, password: String): ByteString =
    hash(s"$identity:$password".asBytes)

  def hash(args: BytesRepresentable*): ByteString = {
    val crypt = MessageDigest.getInstance("SHA-1")
    args.map(_.asBytes.toArray).foreach(crypt.update)
    ByteString(crypt.digest())
  }

  def hashInterleaved(x: BytesRepresentable): ByteString = {
    def merge(x: ByteString, y: ByteString): ByteString =
      ByteString(x.zip(y).flatMap { case (a, b) => ByteString(a, b) }.toArray)

    def filterIndex(condition: (Int) => Boolean)(x: ByteString): ByteString =
      ByteString(x.zipWithIndex.filter { case (_, index) => condition(index) }.map(_._1).toArray)

    def filterEven = filterIndex(_ % 2 == 0) _
    def filterOdd = filterIndex(_ % 2 == 1) _

    val byteString = x.asBytes
    merge(
      hash(filterEven(byteString)),
      hash(filterOdd(byteString))
    )
  }

  def calculateBigA(smallG: BigInt, smallA: BigInt, bigN: BigInt): BigInt = smallG.modPow(smallA, bigN)

  def calculateSmallU(bigA: BigInt, bigB: BigInt): BigInt = hash(bigA, bigB).toUnsignedBigInt
}
