package com.aholg.web.service

import scala.concurrent.Future

trait NoteService {
  def getNotes(id: String): Future[Seq[Note]]
}

case class Note(title: String, content: String)
