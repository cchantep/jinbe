/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe

import java.net.URI

import scala.util.{ Success, Try }

/**
 * Typeclass to try to create an URI from a T
 */
sealed trait URIProvider[T] {
  def apply(config: T): Try[URI]
}

/** [[URIProvider]] factory */
object URIProvider {

  /**
   * Creates a URIProvider from a function.
   */
  def apply[T](f: T => Try[URI]): URIProvider[T] = new URIProvider[T] {
    def apply(config: T): Try[URI] = f(config)
  }

  /**
   * Simple/direct provider
   */
  implicit val idInstance: URIProvider[URI] = URIProvider[URI](Success(_))

  /**
   * Provides an URI from a string
   */
  implicit val fromStringInstance: URIProvider[String] =
    URIProvider[String](s => Try { new URI(s) })
}
