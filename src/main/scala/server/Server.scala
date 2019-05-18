package server

import java.io.File

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Server extends App {
  val confFile = new File("conf/server.conf")
  val system = ActorSystem("server_system", ConfigFactory.parseFile(confFile))
  val worker = system.actorOf(Props[ServerActor], "server_actor")

  println(worker.toString())
}
