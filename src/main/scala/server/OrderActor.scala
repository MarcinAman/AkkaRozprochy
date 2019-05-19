package server

import java.io.{File, FileWriter}
import java.nio.file.Paths

import akka.actor.Actor
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import messages.{Confirmation, Denial, Found, NotFound, SearchRequest}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.io.Source

class OrderActor extends Actor {
  val logger = Logging(context.system, this)
  implicit val t: Timeout = Timeout(5.seconds)
  implicit val ec: ExecutionContext = context.system.dispatcher

  val orderPath = "resources/orders.txt"

  override def receive: Receive = {
    case OrderConfirmation(title) =>
      logger.info(s"Order actor received request to confirm $title")

      val f = new File(orderPath)
      val src = Source.fromFile(f)
      val lines = src.getLines().toList

      val response = lines.find(s => s.startsWith(title)) match {
        case Some(v) => PositiveOrderResponse(v)
        case None => NegativeOrderResponse(title)
      }

      sender ! response
    case OrderRequestRef(value, searchingActor, ref) =>
      logger.info(s"Order actor to order $value")

      val searchRequest = ask(searchingActor, SearchRequestRef(SearchRequest(value.title),None))

      searchRequest.map {
        case Found(title, cost) =>
          val orders = new FileWriter(new File(Paths.get(orderPath).toUri),true)
          orders.append(value.title + "\n")
          orders.close()

          Confirmation(title, cost)
        case NotFound(title) => Denial(title, "Book not found!")

      }.onComplete(s => ref ! s.getOrElse(Denial(value.title, "Internal error")))

    case s => throw WrongRequest(s"Message of type ${s.getClass.getName} not acceptable")
  }
}
