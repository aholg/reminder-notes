package com.aholg.remindernotes.repository


import com.typesafe.config.{Config, ConfigFactory}
import org.flywaydb.core.Flyway
import org.junit.runner.Description
import org.scalatest.{BeforeAndAfterAll, Suite, SuiteMixin}
import org.testcontainers.containers.PostgreSQLContainer
import scalikejdbc.ConnectionPool

import scala.util.Try

trait TestPostgresInstance extends SuiteMixin with BeforeAndAfterAll {
  self: Suite =>

  def connectionPoolName: Symbol

  def config: Config = ConfigFactory.empty()

  private val instance = TestPInstance(Description.createSuiteDescription(self.getClass), config)

  override def beforeAll(): Unit = {
    super.beforeAll()
    ConnectionPool.add(connectionPoolName, instance.jdbcUrl, instance.username, instance.password)
  }

  override protected def afterAll(): Unit = {
    ConnectionPool.close(connectionPoolName)
    instance.stop()
    super.afterAll()
  }
}

private trait TestPInstance {
  def jdbcUrl: String
  def username: String
  def password: String

  def stop(): Unit
}

private object TestPInstance {
  def apply(suiteDescription: Description, config: Config): TestPInstance = {
    if (isInCIEnvironment(config)) {
      new TestPInstance {
        override def jdbcUrl: String = config.getString("db.cod.url")
        override def username: String = config.getString("db.cod.user")
        override def password: String = config.getString("db.cod.password")

        override def stop(): Unit = () // Do nothing

        val flyway = new Flyway()
        flyway.setLocations("classpath:/db/cod/migrations")
        flyway.setDataSource(jdbcUrl, username, password)
        flyway.setIgnoreMissingMigrations(true)
        flyway.migrate()
      }
    } else {
      val container: PostgreSQLContainer[_] = new PostgreSQLContainer("postgres:10.5-alpine")

      container.start()

      val flyway = new Flyway()
      flyway.setDataSource(container.getJdbcUrl, container.getUsername, container.getPassword)
      flyway.setLocations("classpath:/db/notes/migrations")
      flyway.setIgnoreMissingMigrations(true)
      flyway.migrate()

      new TestPInstance {
        override def jdbcUrl: String = container.getJdbcUrl
        override def username: String = container.getUsername
        override def password: String = container.getPassword

        override def stop(): Unit = container.stop()
      }
    }
  }

  private def isInCIEnvironment(config: Config): Boolean = {
    Try(config.getBoolean("running-on-ci")).toOption.contains(true)
  }
}

