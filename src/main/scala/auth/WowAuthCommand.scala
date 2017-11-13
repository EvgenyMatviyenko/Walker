package auth

/**
  * Created by evgenymatviyenko on 11/13/17.
  */
object WowAuthCommand extends Enumeration {
  val AuthLogonChallange = Value(0x00)
  val AuthLogonProof = Value(0x01)
  val AuthReconnectChallenge = Value(0x02)
  val AuthReconnectProof = Value(0x03)
  val AuthRealmList = Value(0x10)
  val XferInitiate = Value(0x30)
  val XferData = Value(0x31)
  val XferAccept = Value(0x32)
  val XferResume = Value(0x33)
  val XferCancel = Value(0x34)
}
