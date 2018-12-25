package com.aholg

import akka.http.scaladsl.server.{HttpApp, Route}
import com.aholg.web.NoteController
import com.aholg.web.repository.{NoteRepository, NoteRepositoryImpl}
import scala.concurrent.ExecutionContext.Implicits.global


class ReminderServer extends HttpApp {
  val noteService: NoteRepository = new NoteRepositoryImpl('notes)
  val noteController = new NoteController(noteService)

  override def routes: Route = noteController.routes
}

object ReminderServer extends App {
  new ReminderServer().startServer("localhost", 8080)
}