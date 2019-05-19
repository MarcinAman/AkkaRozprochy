package server

import java.io.{FileNotFoundException, IOException}

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import messages.{OrderRequest, RequestNotFound, SearchRequest, StreamRequest}

import scala.concurrent.duration._
import scala.language.postfixOps

class ServerActor extends Actor {
  val logger = Logging(context.system, this)

  val streamingActor: ActorRef = context.actorOf(Props[StreamingActor], "streaming_actor")
  val orderActor: ActorRef = context.actorOf(Props[OrderActor], "order_actor")
  val searchingActor: ActorRef = context.actorOf(Props[SearchingActor], "searching_actor")

  override def receive: Receive = {
    case order: OrderRequest => orderActor ! OrderRequestRef(order, searchingActor, context.sender())
    case stream: StreamRequest => streamingActor ! StreamRequestRef(stream, orderActor, context.sender())
    case search: SearchRequest => searchingActor ! SearchRequestRef(search, Some(context.sender()))
    case _ => context.sender() ! RequestNotFound
  }

  override def supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
      case _: FileNotFoundException => Restart
      case _: IOException => Restart
      case _: Exception => Escalate
    }
  }
}
