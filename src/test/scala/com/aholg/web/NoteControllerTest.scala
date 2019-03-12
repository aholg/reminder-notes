package com.aholg.web

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.aholg.web.NoteController.{ErrorResponse, JsonSupport, NoteViewModel}
import com.aholg.web.repository.{Note, NoteRepository}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.Future

class NoteControllerTest extends FunSuite with Matchers with ScalatestRouteTest with SprayJsonSupport with JsonSupport {

  test("should return a note view model") {
    val controller = new NoteController(new NoteRepositoryStub(Future.successful(Seq(Note("WOLOLOOO", "WOLOOOLLOOOLLOOOOOOOOOOOOOOO")))))

    Get("/notes?id=123") ~> Route.seal(controller.routes) ~> check {
      status shouldBe StatusCodes.OK
      responseAs[NoteViewModel] shouldBe NoteViewModel(Seq(Note("WOLOLOOO", "WOLOOOLLOOOLLOOOOOOOOOOOOOOO")))
    }
  }

  test("should return 500 error if notes could not be retrieved") {
    val noteServiceStub = new NoteRepositoryStub(Future.failed(new IllegalArgumentException("how this happen")))
    val controller = new NoteController(noteServiceStub)

    Get("/notes?id=123") ~> Route.seal(controller.routes) ~> check {
      status shouldBe StatusCodes.InternalServerError
      responseAs[ErrorResponse].description should not be empty
    }
  }

  class NoteRepositoryStub(result: Future[Seq[Note]]) extends NoteRepository {
    override def getNotes(id: String): Future[Seq[Note]] = result

    override def addNote(title: String, content: String): Future[Unit] = ???
  }
}
