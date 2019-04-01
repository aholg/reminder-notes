package com.aholg.remindernotes.web

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import com.aholg.remindernotes.repository.{NoteRepository, UserRepository}
import com.aholg.remindernotes.web.Common.{CommonProtocols, ErrorResponse}

import scala.util.{Failure, Success}

//TODO: Separate user functions to separate trait to ease testing
class UserController(noteRepository: UserRepository) extends Directives with CommonProtocols {

  val routes = (post & pathPrefix("users" / "save") & pathEndOrSingleSlash) {
    formFields('userName, 'email, 'password) { (userName, email, password) =>
      onComplete(noteRepository.addUser(userName)) {
        case Success(_) => complete(StatusCodes.Created)
        case Failure(ex) => complete(StatusCodes.InternalServerError, ErrorResponse(ex.getMessage))
      }
    }
  }
}
