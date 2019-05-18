package server

import akka.actor.ActorRef
import messages.{OrderRequest, SearchRequest, StreamRequest}

case class StreamRequestRef(value: StreamRequest, orderActor: ActorRef, ref: ActorRef)

case class OrderRequestRef(value: OrderRequest, searchingActor: ActorRef, ref: ActorRef)

case class SearchRequestRef(value: SearchRequest, ref: ActorRef)



case class OrderConfirmation(title: String)

sealed trait OrderConfirmationResponse

case class PositiveOrderResponse(title: String) extends OrderConfirmationResponse

case class NegativeOrderResponse(title: String) extends OrderConfirmationResponse
