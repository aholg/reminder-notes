package com.aholg.web.repository

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}
import scalikejdbc._

import scala.concurrent.Await
import scala.concurrent.duration._

class NoteRepositoryImplTest extends FunSuite with Matchers with ScalaFutures with TestPostgresInstance with BeforeAndAfterEach {

  import scala.concurrent.ExecutionContext.Implicits.global

  override val connectionPoolName: Symbol = 'default

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(2.seconds, 60.millis)

  override def config: Config = ConfigFactory.parseResources("test_application.conf").resolve

  val noteRepository = new NoteRepositoryImpl(connectionPoolName)

  override protected def afterEach(): Unit = {
    val column = Notes.column
    NamedDB(connectionPoolName).localTx { implicit session =>
      withSQL(
        delete.from(Notes)
      ).update().apply()
    }
  }

  test("should return nothing if note was not found") {
    whenReady(noteRepository.getNotes("someId")) { result =>
      result shouldBe Seq.empty
    }
  }

  test("should return failure if something went wrong") {
    val failureNoteRepository = new NoteRepositoryImpl('WOLOOLOOOLOOO)

    whenReady(failureNoteRepository.getNotes("someId").failed) { result =>
      result shouldBe an[Exception]
    }
  }

  test("should be able to add and return a note") {
    val title = "WOLOOLOOO"
    val content = "WOOLOOOOOOOOOOOOOOLLOOOO"

    Await.ready(noteRepository.addNote(title, content), 1.seconds)

    whenReady(noteRepository.getNotes("someId")) { result =>
      result shouldBe Seq(Note(title, content))
    }
  }

  test("should be able to add and return several notes") {
    val title = "WOLOOLOOO"
    val content = "WOOLOOOOOOOOOOOOOOLLOOOO"

    noteRepository.addNote(title, content)
    Await.ready(noteRepository.addNote(title, content), 1.seconds)

    val note = Note(title, content)

    whenReady(noteRepository.getNotes("someId")) { result =>
      result shouldBe Seq(note, note)
    }
  }
}
