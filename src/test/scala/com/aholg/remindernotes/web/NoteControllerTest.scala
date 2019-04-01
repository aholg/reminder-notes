package com.aholg.remindernotes.web

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.PredefinedToEntityMarshallers._
import akka.http.scaladsl.model.Multipart.FormData
import akka.http.scaladsl.model.Multipart.FormData.BodyPart.Strict
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.aholg.remindernotes.repository.{Note, NoteRepository}
import com.aholg.remindernotes.web.Common.{CommonProtocols, ErrorResponse}
import com.aholg.remindernotes.web.NoteController.{NoteProtocols, NoteViewModel}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.Future

class NoteControllerTest extends FunSuite with Matchers with ScalatestRouteTest with SprayJsonSupport with NoteProtocols with CommonProtocols{

  test("should return a note view model for a user") {
    val noteRepositoryStub = new NoteRepositoryTestFixture {
      override def getNotes(id: String): Future[Seq[Note]] = Future.successful(Seq(Note("WOLOLOOO", "WOLOOOLLOOOLLOOOOOOOOOOOOOOO", "wololool")))
    }

    val controller = new NoteController(noteRepositoryStub)

    Get("/notes?id=123") ~> Route.seal(controller.routes) ~> check {
      status shouldBe StatusCodes.OK
      responseAs[NoteViewModel] shouldBe NoteViewModel(Seq(Note("WOLOLOOO", "WOLOOOLLOOOLLOOOOOOOOOOOOOOO", "wololool")))
    }
  } 

  test("should return 500 error if notes could not be retrieved") {
    val noteRepositoryStub = new NoteRepositoryTestFixture {
      override def getNotes(id: String): Future[Seq[Note]] = Future.failed(new IllegalArgumentException("how this happen"))
    }

    val controller = new NoteController(noteRepositoryStub)

    Get("/notes?id=123") ~> Route.seal(controller.routes) ~> check {
      status shouldBe StatusCodes.InternalServerError
      responseAs[ErrorResponse].description should not be empty
    }
  }

  test("should be able to save a note") {
    val noteRepositoryStub = new NoteRepositoryTestFixture {
      override def addNote(title: String, content: String, userName: String): Future[Unit] = Future.successful(Unit)
    }

    val controller = new NoteController(noteRepositoryStub)

    Post("/notes/save?id=123", FormData(
      Strict("content", "remind me to fucking keeeeelll yoouuuuu"),
      Strict("title", "blood"))) ~> Route.seal(controller.routes) ~> check {
      status shouldBe StatusCodes.Created
    }
  }

  test("should return error if saving note failed") {
    val noteRepositoryStub = new NoteRepositoryTestFixture {
      override def addNote(title: String, content: String, userName: String): Future[Unit] =
        Future.failed(new RuntimeException("OH LORD!!! WHAT IN THE HEAVENS CAN WE DO NOW????"))
    }

    val controller = new NoteController(noteRepositoryStub)

    Post("/notes/save?id=123",
      FormData(
        Strict("content", "remind me to fucking keeeeelll yoouuuuu"),
        Strict("title", "blood"))) ~> Route.seal(controller.routes) ~> check {
      status shouldBe StatusCodes.InternalServerError
      responseAs[ErrorResponse].description should not be empty
    }
  }

  trait NoteRepositoryTestFixture extends NoteRepository {
    override def getNotes(id: String): Future[Seq[Note]] = ???

    override def addNote(title: String, content: String, userName: String): Future[Unit] = ???
  }

}
