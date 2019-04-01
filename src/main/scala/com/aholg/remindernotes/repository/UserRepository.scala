package com.aholg.remindernotes.repository

import scala.concurrent.Future

trait UserRepository {
  def addUser(username: String): Future[Unit]

  def getUser(username: String): Future[Option[User]]
}
