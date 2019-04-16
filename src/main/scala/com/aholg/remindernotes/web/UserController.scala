package com.aholg.remindernotes.web

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import com.aholg.remindernotes.repository.{NoteRepository, User, UserRepository}
import com.aholg.remindernotes.web.Common.{CommonProtocols, ErrorResponse}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.util.{Failure, Success}

//TODO: Separate user functions to separate trait to ease testing
class UserController(noteRepository: UserRepository) extends Directives with CommonProtocols {

  import UserControllerProtocol._

  val routes = concat(addUserRoute, getUserRoute)

  private def addUserRoute = (post & pathPrefix("users" / "save") & pathEndOrSingleSlash) {
    formFields('userName, 'email, 'password) { (userName, email, password) =>
      onComplete(noteRepository.addUser(userName)) {
        case Success(_) => complete(StatusCodes.Created)
        case Failure(ex) => complete(StatusCodes.InternalServerError, ErrorResponse(ex.getMessage))
      }
    }
  }

  private def getUserRoute = (get & pathPrefix("users" / Segment) & pathEndOrSingleSlash) { userId =>
    onComplete(noteRepository.getUser(userId)) {
      case Success(Some(user)) => complete(user)
      case Failure(ex) => complete(StatusCodes.InternalServerError, ErrorResponse(ex.getMessage))
    }
  }
}

object UserControllerProtocol extends DefaultJsonProtocol {
  implicit val userFormat: RootJsonFormat[User] = jsonFormat1(User)
}
