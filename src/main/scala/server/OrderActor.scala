package server

import akka.actor.Actor
import akka.event.Logging

class OrderActor extends Actor {
  val logger = Logging(context.system, this)

  override def receive: Receive = {
    case OrderConfirmation(title) =>
      logger.info(s"Order actor received request to confirm $title")
      sender ! PositiveOrderResponse(title)
    case _ => logger.info("Not implemented in order actor")
  }
}
