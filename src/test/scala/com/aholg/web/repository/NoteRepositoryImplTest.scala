package com.aholg.web.repository

import akka.http.scaladsl.model.headers.LinkParams
import akka.http.scaladsl.model.headers.LinkParams.title
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import org.scalatest.{FunSuite, Matchers}
import scalikejdbc._

class NoteRepositoryImplTest extends FunSuite with Matchers with ScalaFutures with TestPostgresInstance {

  import scala.concurrent.ExecutionContext.Implicits.global

  override val connectionPoolName: Symbol = 'default

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(2.seconds, 60.millis)


  override def config: Config = ConfigFactory.parseResources("test_application.conf").resolve
  val noteRepository = new NoteRepositoryImpl(connectionPoolName)

  test("should return notes for given id") {
    val title = "WOLOOLLOO"
    val content = "WOLOOOLOOOOOOOOOO"

    addNote(title, content)
    whenReady(noteRepository.getNotes("someId")) { result =>
      result shouldBe Seq(Note(title, content)) }
  }

  def addNote(title: String, content: String): Unit = {
    val column = Notes.column
    NamedDB(connectionPoolName).localTx {implicit session =>
      withSQL(
        insertInto(Notes).namedValues(
          column.title -> title,
          column.content -> content
        )
      ).update().apply()
    }
  }
}
