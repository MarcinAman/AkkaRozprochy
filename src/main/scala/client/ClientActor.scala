package client

import akka.actor.Actor
import akka.event.Logging
import messages.Request

class ClientActor extends Actor with akka.actor.ActorLogging {
  val logger = Logging(context.system, this)

  override def receive: Receive = {
    case v: Request =>
      val selection = context.actorSelection("akka.tcp://server_system@127.0.0.1:3552/user/server_actor")
      logger.info(s"Sending message $v to server")
      selection ! v
    case s => logger.info(s.toString)
  }
}
