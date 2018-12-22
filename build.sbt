
lazy val root = (project in file(".")).settings(
  name := "reminder-notes",
  scalaVersion := "2.12.7",
  organization := "com.aholg",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.1.6",
    "com.typesafe.akka" %% "akka-stream" % "2.5.19",
    "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.19" % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % "10.1.6" % Test,
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.6",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
)
