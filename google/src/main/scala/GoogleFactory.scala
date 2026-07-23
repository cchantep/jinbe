/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe.google

import java.net.URI

import play.api.libs.ws.ahc.StandaloneAhcWSClient

import io.github.cchantep.jinbe.ObjectStorage
import io.github.cchantep.jinbe.spi.{ Injector, StorageFactory, StorageScheme }

/**
 * This factory is using `javax.inject`
 * to resolve `play.api.libs.ws.ahc.StandaloneAhcWSClient`.
 */
final class GoogleFactory extends StorageFactory {

  @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
  def apply(injector: Injector, uri: URI): ObjectStorage = {
    @inline implicit def ws: StandaloneAhcWSClient =
      injector.instanceOf(classOf[StandaloneAhcWSClient])

    GoogleStorage(GoogleTransport[URI](uri).get)
  }
}

/** Storage scheme for Google Cloud Storage */
final class GoogleScheme extends StorageScheme {
  val scheme = "google"

  @inline
  def factoryClass: Class[_ <: StorageFactory] = classOf[GoogleFactory]
}
