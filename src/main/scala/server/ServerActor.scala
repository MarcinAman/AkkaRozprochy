package server

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import messages.{OrderRequest, RequestNotFound, SearchRequest, StreamRequest}

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
}
