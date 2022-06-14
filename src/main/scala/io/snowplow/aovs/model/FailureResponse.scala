package io.snowplow.aovs.model

case class FailureResponse(action: Action, id: String, status: Status, message: String)
