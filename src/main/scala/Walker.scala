import java.net.InetSocketAddress

import akka.actor.{Actor, ActorSystem, Props}
import auth._
import config.Config
import socket._

/**
  * Created by evgenymatviyenko on 11/11/17.
  */

class Listener extends Actor {
  override def receive: Receive = {
    case SocketConnected =>
      println("Socket connected.")
      sender() ! SocketWrite(new WowAuthClientPacket(AuthLogonChallenge("SilverDefender")).asBytes)
    case SocketDisconnected =>
      println("Socket disconnected.")
    case SocketCommandFailed =>
      println("Socket command failed.")
    case SocketDataReceived(data) =>
      val packet = new WowAuthServerPacket(data.toArray)
      println(s"Socket received packet $packet with content ${packet.content}.")
    case SocketUnknownEvent(event) =>
      println(s"Socket received unknown event $event.")
  }
}

object Walker extends App {
  val system = ActorSystem("system")
  val socketListener = system.actorOf(Props[Listener])
  val socket = system.actorOf(Props(new WalkerSocket(socketListener)))
  socket ! SocketConnect(new InetSocketAddress(Config.realmlist, 3724))
}
