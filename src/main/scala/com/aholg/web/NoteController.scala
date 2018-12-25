package com.aholg.web

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import com.aholg.web.NoteController.{ErrorResponse, JsonSupport, NoteViewModel}
import com.aholg.web.repository.{Note, NoteRepository}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.util.{Failure, Success}

case class NoteController(noteService: NoteRepository) extends Directives with JsonSupport {
  lazy val routes = {
    (path("notes") & pathEndOrSingleSlash) {
      get {
        onComplete(noteService.getNotes("")) {
          case Success(result) => complete(NoteViewModel(result))
          case Failure(ex) => complete(StatusCodes.InternalServerError, ErrorResponse(ex.getMessage))
        }
      }
    }
  }
}

object NoteController {

  case class NoteViewModel(notes: Seq[Note])

  case class ErrorResponse(description: String)

  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val noteFormat: RootJsonFormat[Note] = jsonFormat2(Note.apply)
    implicit val noteViewModelFormat: RootJsonFormat[NoteViewModel] = jsonFormat1(NoteViewModel)
    implicit val errorResponseFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse)
  }

}