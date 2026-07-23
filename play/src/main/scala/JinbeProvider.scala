/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
 * Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package play.modules.jinbe

import java.net.URI
import javax.inject._

import scala.util.{ Failure, Try }

import akka.stream.Materializer

import play.api.libs.ws.ahc.StandaloneAhcWSClient

import io.github.cchantep.jinbe.ObjectStorage
import io.github.cchantep.jinbe.spi.{ Injector, StorageFactory }

private[jinbe] final class JinbeProvider(
    factoryClass: Class[_ <: StorageFactory],
    uri: URI)
    extends Provider[ObjectStorage] {

  @SuppressWarnings(
    Array("org.wartremover.warts.Var", "org.wartremover.warts.Null")
  )
  @Inject var jinbeInjector: Injector = _

  lazy val get: ObjectStorage = {
    val factory = jinbeInjector.instanceOf(factoryClass)

    factory(jinbeInjector, uri)
  }
}

private[jinbe] object JinbeProvider {

  def from(configUri: URI): Try[JinbeProvider] =
    JinbeConfig.registry.factoryClass(configUri.getScheme) match {
      case Some(cls) => Try(new JinbeProvider(cls, configUri))

      case _ =>
        Failure[JinbeProvider](
          new IllegalArgumentException(
            s"Unsupported storage URI: ${configUri.toString}"
          )
        )
    }
}

private[jinbe] final class PlayInjectorProvider extends Provider[Injector] {

  @SuppressWarnings(
    Array("org.wartremover.warts.Null", "org.wartremover.warts.Var")
  )
  @Inject var injector: play.api.inject.Injector = _

  lazy val get: Injector = new PlayInjector(injector)
}

/** Utility to bind Jinbe injector abstraction with Play implementation. */
final class PlayInjector(
    underlying: play.api.inject.Injector)
    extends Injector {
  def instanceOf[T](cls: Class[T]): T = underlying.instanceOf[T](cls)
}

private[jinbe] final class WSProvider extends Provider[StandaloneAhcWSClient] {

  @SuppressWarnings(
    Array("org.wartremover.warts.Null", "org.wartremover.warts.Var")
  )
  @Inject var materializer: Materializer = _

  lazy val get: StandaloneAhcWSClient = {
    implicit def m: Materializer = materializer

    StandaloneAhcWSClient()
  }
}
