ThisBuild /  organization := "com.aholg"
  ThisBuild / scalaVersion := "2.12.8"

lazy val root = (project in file("."  )).settings(
  name := "reminder-notes",
  libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.6"
)
