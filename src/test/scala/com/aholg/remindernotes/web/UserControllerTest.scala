package com.aholg.remindernotes.web

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.PredefinedToEntityMarshallers._
import akka.http.scaladsl.model.Multipart.FormData
import akka.http.scaladsl.model.Multipart.FormData.BodyPart.Strict
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.aholg.remindernotes.repository.{Note, NoteRepository, User, UserRepository}
import com.aholg.remindernotes.web.Common.{CommonProtocols, ErrorResponse}
import com.aholg.remindernotes.web.NoteController.{NoteProtocols, NoteViewModel}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.Future

class UserControllerTest extends FunSuite with ScalatestRouteTest with SprayJsonSupport with Matchers {

  import UserControllerProtocol._

  val controller = new UserController(new UserRepository {
    override def addUser(username: String): Future[Unit] = Future.successful(Unit)

    override def getUser(username: String): Future[Option[User]] = Future.successful(Some(User(username)))
  })

  test("should be able to save a user") {
    Post("/users/save",
      FormData(
        Strict("userName", "remind me to fucking keeeeelll yoouuuuu"),
        Strict("email", "blood"),
        Strict("password", "blood"))) ~> Route.seal(controller.routes) ~> check {
      status shouldBe StatusCodes.Created
    }
  }

  test("should be able to retrieve a user") {
    Get("/users/FartBlaster69") ~> Route.seal(controller.routes) ~> check {
      status shouldBe StatusCodes.OK
      responseAs[User] shouldEqual User("FartBlaster69")
    }
  }
}
