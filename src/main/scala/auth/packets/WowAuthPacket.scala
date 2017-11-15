package auth.packets

import akka.util.ByteString
import auth._
import utils.ByteHelpers._

/**
  * Created by evgenymatviyenko on 11/13/17.
  */
abstract class WowAuthPacket(val command: WowAuthCommand.Value) { }