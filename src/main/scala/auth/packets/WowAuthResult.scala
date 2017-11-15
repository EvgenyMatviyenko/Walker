package auth.packets

import scala.util.{Success, Try}

/**
  * Created by evgenymatviyenko on 11/13/17.
  */
sealed trait WowAuthResult { }
object WowAuthResult {
  def apply(byte: Byte): WowAuthResult = {
    Try(WowAuthSuccessResultCode(byte)) match {
      case Success(code) => WowAuthSuccessResult(code)
      case _ => {
        Try(WowAuthFailureResultCode(byte)) match {
          case Success(code) => WowAuthFailureResult(code)
          case _ => WowAuthUnknownResult()
        }
      }
    }
  }
}

case class WowAuthSuccessResult(code: WowAuthSuccessResultCode.Value) extends WowAuthResult
case class WowAuthFailureResult(code: WowAuthFailureResultCode.Value) extends WowAuthResult
case class WowAuthUnknownResult() extends WowAuthResult

object WowAuthSuccessResultCode extends Enumeration {
  val Success = Value(0x00)
  val SuccessSurvey = Value(0x0E)
}

object WowAuthFailureResultCode extends Enumeration {
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
  val ParentControl = Value(0x0F)
  val LockedEnforced = Value(0x10)
  val TrialEnded = Value(0x11)
  val UseBattleNet = Value(0x12)
}