/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
 * Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package play.modules.jinbe

import java.net.URI
import javax.inject._

import scala.collection.immutable.Set

import play.api._
import play.api.inject.{ Binding, BindingKey, Module }
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import io.github.cchantep.jinbe.ObjectStorage
import io.github.cchantep.jinbe.spi.Injector

/**
 * Jinbe module.
 */
@Singleton
final class JinbeModule extends Module {

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def bindings(
      environment: Environment,
      configuration: Configuration
    ): Seq[Binding[_]] =
    bind[Injector].toProvider[PlayInjectorProvider] +:
      bind[StandaloneAhcWSClient].toProvider[WSProvider] +:
      apiBindings(JinbeConfig parse configuration).toSeq

  private def apiBindings(
      info: Set[(String, URI)]
    ): Set[Binding[ObjectStorage]] = info.flatMap {
    case (name, uri) =>
      val provider: Provider[ObjectStorage] = {
        @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
        val result = JinbeProvider.from(uri).get

        result
      }

      val annot: NamedStorage = new NamedStorageImpl(name)
      val bs = List(JinbeModule.key(name).qualifiedWith(annot) to provider)

      if (name == "default") {
        bind[ObjectStorage].to(provider) :: bs
      } else {
        bs
      }
  }
}

private[jinbe] object JinbeModule {

  def key(name: String): BindingKey[ObjectStorage] =
    BindingKey(classOf[ObjectStorage]).qualifiedWith(new NamedStorageImpl(name))

}
