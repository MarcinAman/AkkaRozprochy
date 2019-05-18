package server

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.stream.ActorMaterializer


class StreamingActor extends Actor {

  val logger = Logging(context.system, this)
  implicit val mat: ActorMaterializer = ActorMaterializer()

  override def receive: Receive = {
    case StreamRequestRef(v, orderActor, client) =>
      logger.info("Streaming actor received request for: " + v.title)

      context.actorOf(Props[StreamingActorWorker]) ! StreamRequestRef(v, orderActor, client)
    case _ => throw WrongRequest("Message not acceptable")
  }
}
