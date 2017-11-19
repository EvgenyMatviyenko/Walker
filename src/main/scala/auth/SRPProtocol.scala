package auth

import java.security.MessageDigest

import akka.util.ByteString

import scala.util.Random

import utils.ByteHelpers._

/**
  * Created by evgenymatviyenko on 11/17/17.
  */
object SRPProtocol {
  def calculate(bigN: BigInt, g: BigInt, bigB: BigInt, s: BigInt, identity: String, password: String): (BigInt, BigInt) = {
    val k = hash(bigN, g).toUnsignedBigInt
    val a = cryptrand(1024, bigN)
    val bigA = g.modPow(a, bigN)
    val u = hash(bigA, bigB).toUnsignedBigInt
    val x = hash(s, hash((identity ++ ":" ++ password).asBytes)).toUnsignedBigInt
    //val bigS = (bigB - g.modPow(x, bigN)).modPow(a + u * x, bigN)
    val bigS = (bigB - k * g.modPow(x, bigN)).modPow(a + u * x, bigN)
    val bigK = hash(bigS)
    val bigM = hash(hash(bigN).toUnsignedBigInt ^ hash(g).toUnsignedBigInt, hash(identity.asBytes), s, bigA, bigB, bigK)
    (bigA, bigM.toUnsignedBigInt)
  }

  private def cryptrand(n: Int, bigN: BigInt) = BigInt(n, Random) % bigN

  private trait Hashable {
    def toByteArray: Array[Byte]
  }

  private implicit class ByteStringHashable(x: ByteString) extends Hashable {
    override def toByteArray: Array[Byte] = x.toArray
  }

  private implicit class BigIntHashable(x: BigInt) extends Hashable {
    override def toByteArray: Array[Byte] = x.asBytes.toArray
  }

  private def hash(args: Hashable*): ByteString = {
    val crypt = MessageDigest.getInstance("SHA-1")
    args.map(_.toByteArray).foreach(crypt.update)
    ByteString(crypt.digest())
  }
}
