package com.aholg.web.repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scalikejdbc._

class NoteRepositoryImpl(connectionPoolName: Symbol)(implicit ec: ExecutionContext) extends NoteRepository {

  import Notes._

  override def addNote(title: String, content: String) = {
    val column = Notes.column

    Future {
      NamedDB(connectionPoolName) localTx { implicit session =>
        withSQL {
          insertInto(Notes).namedValues(
            column.title -> title,
            column.content -> content
          )
        }.update().apply()
      }
    }
  }


  override def getNotes(id: String): Future[Seq[Note]] = {
    Future {
      NamedDB(connectionPoolName) readOnly { implicit session =>
        withSQL {
          select.from(Notes as n)
        }.map(Notes(n.resultName)).single().list().apply()
      }
    }
  }
}

case class Note(title: String, content: String)

object Notes extends SQLSyntaxSupport[Note] {
  def apply(note: ResultName[Note])(rs: WrappedResultSet): Note = new Note(rs.get(note.title), rs.get(note.content))

  val n = Notes.syntax("n")
}
