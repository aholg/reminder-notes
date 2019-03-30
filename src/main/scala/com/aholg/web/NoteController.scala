package com.aholg.web

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.LinkParams.title
import akka.http.scaladsl.server._
import com.aholg.web.NoteController.{ErrorResponse, JsonSupport, NoteViewModel}
import com.aholg.web.repository.{Note, NoteRepository}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.util.{Failure, Success}

case class NoteController(noteService: NoteRepository) extends Directives with JsonSupport {


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

  case class ErrorResponse(description: String)

  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val noteFormat: RootJsonFormat[Note] = jsonFormat3(Note.apply)
    implicit val noteViewModelFormat: RootJsonFormat[NoteViewModel] = jsonFormat1(NoteViewModel)
    implicit val errorResponseFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse)
  }

}