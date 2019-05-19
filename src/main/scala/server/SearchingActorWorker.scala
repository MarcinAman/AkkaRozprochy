package server

import java.io.File

import akka.actor.Actor
import akka.event.Logging
import messages.{Found, NotFound, SearchRequest}

import scala.io.Source

class SearchingActorWorker(dbIndex: Int) extends Actor {
  val logger = Logging(context.system, this)

  private val dbPath = s"resources/db$dbIndex.txt"

  override def receive: Receive = {
    case SearchRequest(title) =>
      val f = new File(dbPath)
      val src = Source.fromFile(f)
      val lines = src.getLines().toList

      src.close()

      lines.find(s => s.startsWith(title)) match {
        case Some(value) => sender ! Found(title, value.split(" ")(1).toDouble)
        case None => sender ! NotFound(title)
      }

    case s => throw WrongRequest(s"Message of type ${s.getClass.getName} not acceptable")
  }
}
