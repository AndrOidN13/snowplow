package io.snowplow.aovs.utils

import org.slf4j.Logger

import scala.concurrent.{ExecutionContext, Future}

trait Retrying {
  def retryAndLogError[T](retries: Int = 3, errorMessage: String, logger: Logger)(f: => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    f.recoverWith {
      case e: Exception =>
        if(retries > 0) retryAndLogError(retries - 1, errorMessage, logger)(f)
        else {
          logger.error(errorMessage, e)
          f
        }
    }
  }
}
