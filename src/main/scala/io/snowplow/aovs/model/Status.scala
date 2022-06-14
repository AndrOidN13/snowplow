package io.snowplow.aovs.model

import io.circe.Encoder
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCode

sealed trait Status {
  val statusCode: StatusCode
}
sealed trait Success extends Status
sealed trait Error extends Status
case object Success extends Success {
  val statusCode = StatusCodes.OK
}
case object Created extends Success {
  val statusCode = StatusCodes.Created
}
case object InternalServerError extends Error {
  val statusCode = StatusCodes.InternalServerError
}
case object BadRequest extends Error {
  val statusCode = StatusCodes.BadRequest
}
case object NotFound extends Error {
  val statusCode = StatusCodes.NotFound
}

object Status {
  implicit val encodeStatus: Encoder[Status] = Encoder[String].contramap {
    case Success | Created                             => "success"
    case InternalServerError | BadRequest | NotFound   => "error"
  }
}
