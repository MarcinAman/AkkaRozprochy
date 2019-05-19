package server

import akka.actor.{Actor, PoisonPill}
import akka.event.Logging
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.language.postfixOps

class StreamingActorWorker extends Actor {

  val logger = Logging(context.system, this)
  implicit val mat: ActorMaterializer = ActorMaterializer()

  override def receive: Receive = {
    case StreamRequestRef(v, orderActor, client) =>
      logger.info("Streaming actor worker received request for: " + v.title)

      implicit val timeout: Timeout = Timeout(5.seconds)

      Source.fromFuture {
        ask(orderActor, OrderConfirmation(v.title))
      }.map {
        case PositiveOrderResponse(title) => new BookContentProvider().provideContent(title)
        case NegativeOrderResponse(title) => throw new RuntimeException("Title " + title + " wasn't ordered")
      }.flatMapConcat(s => Source.fromIterator(() => s))
        .throttle(20, 1 seconds)
        .via(Flow[String].alsoTo(Sink.onComplete(_ => self ! PoisonPill)))
        .to(Sink.actorRef(client, "END"))
        .run()

    case s => throw WrongRequest(s"Message of type ${s.getClass.getName} not acceptable")
  }
}
