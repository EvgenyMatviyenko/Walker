package auth

/**
  * Created by evgenymatviyenko on 11/13/17.
  */
object WowAuthResult extends Enumeration {
  val Success = Value(0x00)
  val Unknown0 = Value(0x01)
  val Unknown1 = Value(0x02)
  val Banned = Value(0x03)
  val UnknownAccount = Value(0x04)
  val IncorrectPassword = Value(0x05)
  val AlreadyOnline = Value(0x06)
  val NoTime = Value(0x07)
  val DbBusy = Value(0x08)
  val VersionInvalid = Value(0x09)
  val VersionUpdate = Value(0x0A)
  val InvalidServer = Value(0x0B)
  val Suspended  = Value(0x0C)
  val NoAccess = Value(0x0D)
  val SuccessSurvey = Value(0x0E)
  val ParentControl = Value(0x0F)
  val LockedEnforced = Value(0x10)
  val TrialEnded = Value(0x11)
  val UseBattleNet = Value(0x12)
}