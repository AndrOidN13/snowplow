package io.snowplow.aovs.model

import io.circe.{Decoder, Encoder}

sealed trait Action
case object UploadSchema extends Action {
  override val toString: String = "uploadSchema"
}
case object DownloadSchema extends Action {
  override val toString: String = "downloadSchema"
}
case object ValidateDocument extends Action {
  override val toString: String = "validateDocument"
}

object Action {
  implicit val decodeAction: Decoder[Action] = Decoder[String].emap {
    case UploadSchema.toString     => Right(UploadSchema)
    case DownloadSchema.toString   => Right(DownloadSchema)
    case ValidateDocument.toString => Right(ValidateDocument)
    case other                     => Left(s"Invalid action: $other")
  }

  implicit val encodeAction: Encoder[Action] = Encoder[String].contramap {
    case UploadSchema     => UploadSchema.toString
    case DownloadSchema   => DownloadSchema.toString
    case ValidateDocument => ValidateDocument.toString
  }
}
