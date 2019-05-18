package client

import java.io.File

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.annotation.tailrec
import scala.io.StdIn.readLine

object Client extends App {
  val confFile = new File("conf/client.conf")
  val system = ActorSystem("client_system", ConfigFactory.parseFile(confFile))
  val worker = system.actorOf(Props[ClientActor], "client_actor")

  println(worker.toString())

  @tailrec
  def loop(): Unit = {

    val userInput: String = readLine()

    ClientInputParser.parse(userInput) match {
      case Right(value) => worker ! value
      case Left(value) => println(value)
    }

    loop()
  }

  loop()
}
