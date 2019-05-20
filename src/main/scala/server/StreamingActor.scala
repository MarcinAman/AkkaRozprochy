package server

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import akka.stream.ActorMaterializer
import messages.StreamRequest

import scala.concurrent.duration._


class StreamingActor extends Actor {

  val logger = Logging(context.system, this)
  implicit val mat: ActorMaterializer = ActorMaterializer()

  override def receive: Receive = {
    case streamRequest: StreamRequest =>
      logger.info("Streaming actor received request for: " + streamRequest.title)

      context.actorOf(Props[StreamingActorWorker]).forward(streamRequest)
    case s => throw WrongRequest(s"Message of type ${s.getClass.getName} not acceptable")
  }

  override def supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
      case _: Exception => Restart
    }
  }
}
