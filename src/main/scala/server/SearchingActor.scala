package server

import akka.actor.Actor
import akka.event.Logging

class SearchingActor extends Actor {
  val logger = Logging(context.system, this)

  override def receive: Receive = {
    case SearchRequestRef(value, ref) =>
    case _ => logger.info("Not implemented in searching actor")
  }
}
