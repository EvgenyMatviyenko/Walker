import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import auth.SRPProtocol
import auth.packets._
import config.Config
import socket._
import utils.ByteHelpers._

/**
  * Created by evgenymatviyenko on 11/11/17.
  */

class Listener extends Actor {
  override def receive: Receive = {
    case SocketConnected =>
      println("Socket connected.")
      sendPacket(new WowAuthClientPacket(AuthLogonChallenge("SilverDefender")))
    case SocketDisconnected =>
      println("Socket disconnected.")
    case SocketCommandFailed =>
      println("Socket command failed.")
    case SocketDataReceived(data) =>
      val packet = new WowAuthServerPacket(data)
      println(s"Socket received packet $packet.")
      packet.content match {
        case AuthEmptyResponse() =>
          println(s"Socket received empty response.")
        case AuthUnknownResponse() =>
          println("Socket received unknown response.")
        case response @ AuthLogonChallengeResponse(_) =>
          val (bigA, bigM) = SRPProtocol.calculate(response.bigN, response.g, response.bigB, response.s, "SilverDefender", "Evgeny32165487")
          sendPacket(new WowAuthClientPacket(AuthLogonProof(bigA, bigM)))
      }
    case SocketUnknownEvent(event) =>
      println(s"Socket received unknown event $event.")
  }

  private def sendPacket(packet: WowAuthClientPacket): Unit = {
    println(s"Socket sent packet $packet.")
    sender() ! SocketWrite(packet.asBytes)
  }
}

object Walker extends App {
  val system = ActorSystem("system")
  val socketListener = system.actorOf(Props[Listener])
  val socket = system.actorOf(Props(new WalkerSocket(socketListener)))
  socket ! SocketConnect(new InetSocketAddress(Config.realmlist, 3724))
}
