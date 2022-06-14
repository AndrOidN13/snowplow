package io.snowplow.aovs.utils

import org.slf4j.{Logger, LoggerFactory}

/**
 * Provides an instance of [[org.slf4j.Logger]] which can be used to log messages
 */
trait AppLogger {
  /** A [[org.slf4j.Logger]] named after the actual class */
  protected val logger: Logger = LoggerFactory.getLogger(this.getClass)
}
