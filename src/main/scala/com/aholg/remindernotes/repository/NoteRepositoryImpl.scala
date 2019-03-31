package com.aholg.remindernotes.repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scalikejdbc._

class NoteRepositoryImpl(connectionPoolName: Symbol)(implicit ec: ExecutionContext) extends NoteRepository {

  import Notes._
  import Users._

  override def addNote(title: String, content: String, userName: String) = {
    val column = Notes.column

    Future {
      NamedDB(connectionPoolName) localTx { implicit session =>
        withSQL {
          insertInto(Notes).namedValues(
            column.title -> title,
            column.content -> content,
            column.username -> userName
          )
        }.update().apply()
      }
    }
  }

  override def getNotes(username: String): Future[Seq[Note]] = {
    Future {
      NamedDB(connectionPoolName) readOnly { implicit session =>
        withSQL {
          select.from(Notes as n).where.eq(n.username, username)
        }.map(Notes(n.resultName)).single().list().apply()
      }
    }
  }

  override def addUser(username: String): Future[Unit] = {
    val column = Users.column

    Future {
      NamedDB(connectionPoolName) localTx { implicit session =>
        withSQL {
          insertInto(Users).namedValues(column.username -> username)
        }.update().apply()
      }
    }
  }

  override def getUser(username: String): Future[Option[User]] = {
    Future {
      NamedDB(connectionPoolName) readOnly { implicit session =>
        withSQL {
          select.from(Users as u).where.eq(u.username, username)
        }.map(Users(u.resultName)).single().apply()
      }
    }
  }
}

case class Note(title: String, content: String, username: String)

object Notes extends SQLSyntaxSupport[Note] {
  def apply(note: ResultName[Note])(rs: WrappedResultSet): Note = new Note(rs.get(note.title), rs.get(note.content), rs.get(note.username))

  val n = Notes.syntax("n")
}

case class User(username: String)

object Users extends SQLSyntaxSupport[User] {
  def apply(user: ResultName[User])(rs: WrappedResultSet): User = new User(rs.get(user.username))

  val u = Users.syntax("u")
}