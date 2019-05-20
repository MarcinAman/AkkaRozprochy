package server

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import messages.{Found, NotFound, SearchRequest}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class SearchingActor extends Actor {
  val logger = Logging(context.system, this)

  implicit val ec: ExecutionContext = context.system.dispatcher
  implicit val t: Timeout = Timeout(5.seconds)
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val worker1: ActorRef = context.actorOf(Props(new SearchingActorWorker(1)), "searching_worker_1")
  val worker2: ActorRef = context.actorOf(Props(new SearchingActorWorker(2)), "searching_worker_2")

  override def receive: Receive = {
    case searchRequest: SearchRequest =>
      logger.info(s"Searching actor received message: $searchRequest")

      val response = for {
        r1 <- ask(worker1, searchRequest)
        r2 <- ask(worker2, searchRequest)
      } yield (r1, r2)

      val extracted = response.map {
        case (Found(title,price), _) => Found(title, price)
        case (_, Found(title, price)) => Found(title, price)
        case _ => NotFound(searchRequest.title)
      }

      extracted.onComplete{
        case Failure(exception) => sender ! NotFound("Exception: " + exception)
        case Success(v) => sender ! v
      }
    case s => throw WrongRequest(s"Message of type ${s.getClass.getName} not acceptable")
  }

  override def supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
      case _: Exception => Restart
    }
  }
}
