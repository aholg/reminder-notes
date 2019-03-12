package com.aholg.web.repository

import scala.concurrent.Future

trait NoteRepository {
  def getNotes(id: String): Future[Seq[Note]]

  def addNote(title: String, content: String): Future[Unit]
}
