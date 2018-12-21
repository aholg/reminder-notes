package com.aholg.web

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model. HttpEntity
import akka.http.scaladsl.server.HttpApp
import akka.http.scaladsl.server.Route

class ReminderServer extends HttpApp {
    override def routes: Route = {
      (pathPrefix("well" / "hello") & path("there")) {
          get {
            complete(HttpEntity(`text/html(UTF-8)`, "well hello there!"))
          }
        }
    }
}

object ReminderServer extends App {
    new ReminderServer().startServer("localhost", 8080)
}