package com.aholg.remindernotes.repository

import scala.concurrent.Future

trait NoteRepository {
  def getNotes(username: String): Future[Seq[Note]]

  def addNote(title: String, content: String, userName: String): Future[Unit]

  def addUser(username: String): Future[Unit]

  def getUser(username: String): Future[Option[User]]
}
