package com.aholg.remindernotes.web

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import com.aholg.remindernotes.repository.{Note, NoteRepository}
import com.aholg.remindernotes.web.Common.{CommonProtocols, ErrorResponse}
import com.aholg.remindernotes.web.NoteController.{NoteProtocols, NoteViewModel}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.util.{Failure, Success}

case class NoteController(noteService: NoteRepository) extends Directives with NoteProtocols with CommonProtocols {

  lazy val routes = {
    concat(notesViewRoute, saveNoteRoute)
  }

  private def notesViewRoute =
    (path("notes") & parameter('id) & get) { userName => {
      onComplete(noteService.getNotes(userName)) {
        case Success(result) => complete(NoteViewModel(result))
        case Failure(ex) => complete(StatusCodes.InternalServerError, ErrorResponse(ex.getMessage))
      }
    }
    }

  def saveNoteRoute =
    (pathPrefix("notes" / "save") & parameter('id) & post) { userName => {
      formFields('title, 'content) { (title, content) =>
        onComplete(noteService.addNote(title, content, userName)) {
          case Success(result) => complete(StatusCodes.Created)
          case Failure(ex) =>
            //TODO: Add logging
            complete(StatusCodes.InternalServerError, ErrorResponse(ex.getMessage))
        }
      }
    }
    }
}

object NoteController {

  case class NoteViewModel(notes: Seq[Note])

  trait NoteProtocols extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val noteFormat: RootJsonFormat[Note] = jsonFormat3(Note.apply)
    implicit val noteViewModelFormat: RootJsonFormat[NoteViewModel] = jsonFormat1(NoteViewModel)
  }

}