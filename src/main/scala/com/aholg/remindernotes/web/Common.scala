package com.aholg.remindernotes.web

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object Common {
  case class ErrorResponse(description: String)

  trait CommonProtocols extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val errorResponseFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse)
  }
}
