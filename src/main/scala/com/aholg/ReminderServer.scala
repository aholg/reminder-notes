package com.aholg

import akka.http.scaladsl.server.{HttpApp, Route}
import com.aholg.remindernotes.web.{NoteController, UserController}
import com.aholg.remindernotes.repository.{NoteRepository, NoteRepositoryImpl}

import scala.concurrent.ExecutionContext.Implicits.global


class ReminderServer extends HttpApp {
  val noteService = new NoteRepositoryImpl('notes)
  val noteController = new NoteController(noteService)

  val userController= new UserController(noteService)

  override def routes: Route = concat(noteController.routes, userController.routes)
}

object ReminderServer extends App {
  new ReminderServer().startServer("localhost", 8080)
}