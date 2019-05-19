package server

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import messages.{Found, NotFound}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class SearchingActor extends Actor {
  val logger = Logging(context.system, this)

  implicit val ec: ExecutionContext = context.system.dispatcher
  implicit val t: Timeout = Timeout(5.seconds)
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val worker1: ActorRef = context.actorOf(Props(new SearchingActorWorker(1)), "searching_worker_1")
  val worker2: ActorRef = context.actorOf(Props(new SearchingActorWorker(2)), "searching_worker_2")

  override def receive: Receive = {
    case SearchRequestRef(value, ref) =>

      val response = for {
        r1 <- ask(worker1, value)
        r2 <- ask(worker2, value)
      } yield (r1, r2)

      val extracted = response.map {
        case (Found(title,price), _) => Found(title, price)
        case (_, Found(title, price)) => Found(title, price)
        case _ => NotFound(value.title)
      }

      extracted.onComplete(s => ref ! s.get)
    case _ => logger.info("Not implemented in searching actor")
  }
}
