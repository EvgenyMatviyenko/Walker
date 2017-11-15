package socket

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.{IO, Tcp}
import akka.io.Tcp._
import akka.util.ByteString

sealed trait WalkerSocketEvent { }
case object SocketConnected extends WalkerSocketEvent
case object SocketDisconnected extends WalkerSocketEvent
case object SocketCommandFailed extends WalkerSocketEvent
case class SocketDataReceived(data: ByteString) extends WalkerSocketEvent
case class SocketUnknownEvent(event: Any) extends WalkerSocketEvent

sealed trait WalkerSocketCommand { }
case class SocketConnect(address: InetSocketAddress) extends WalkerSocketCommand
case object SocketDisconnect extends WalkerSocketCommand
case class SocketWrite(data: ByteString) extends WalkerSocketCommand

class WalkerSocket(listener: ActorRef) extends Actor {
  import context.system

  def receive: Receive = idle

  def idle: Receive = {
    case SocketConnect(address) =>
      IO(Tcp) ! Connect(address)
      context become connecting
  }

  def connecting: Receive = {
    case CommandFailed =>
      listener ! SocketCommandFailed
      context become idle

    case c @ Connected(_, _) =>
      listener ! SocketConnected
      sender() ! Register(self)
      context become connected(sender())

    case unknown =>
      listener ! SocketUnknownEvent(unknown)
  }

  def connected(socket: ActorRef): Receive = {
    case SocketWrite(data) =>
      socket ! Write(data)

    case CommandFailed =>
      listener ! SocketCommandFailed

    case Received(data) =>
      listener ! SocketDataReceived(data)

    case _: ConnectionClosed =>
      listener ! SocketDisconnected
  }
}
