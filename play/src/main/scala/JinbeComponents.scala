/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
 * Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package play.modules.jinbe

import java.net.URI

import io.github.cchantep.jinbe.ObjectStorage

/** Jinbe components contract */
trait JinbeComponents {

  /** The instance name (default: `default`) */
  def name: String

  /** The connection URI */
  def parsedUri: URI

  /** The ObjectStorage initialized according the current configuration */
  def jinbe: ObjectStorage
}

/**
 * Default implementation of [[JinbeComponents]].
 *
 * {{{
 * import play.api.ApplicationLoader
 *
 * import play.modules.jinbe._
 *
 * abstract class OtherComponentsFromContext(
 *   context: ApplicationLoader.Context
 * ) extends play.api.BuiltInComponentsFromContext(context) {
 *   // other components
 * }
 *
 * class MyComponent2(
 *   context: ApplicationLoader.Context,
 *   val name: String, // Name of the storage config (see next section)
 *   val parsedUri: java.net.URI // Jinbe URI for this component
 * ) extends OtherComponentsFromContext(context) with JinbeComponentsWithInjector {
 *   // can be a Controller, a Play custom Module, ApplicationLoader ...
 *
 *   def jinbeInjector = new play.modules.jinbe.PlayInjector(injector)
 *   def httpFilters: Seq[play.api.mvc.EssentialFilter] = ???
 *   def router: play.api.routing.Router = ???
 * }
 * }}}
 */
trait JinbeComponentsWithInjector extends JinbeComponents {

  /** The injector used to resolve the storage dependencies */
  def jinbeInjector: io.github.cchantep.jinbe.spi.Injector

  final lazy val jinbe: ObjectStorage = {
    @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
    def provider: JinbeProvider = JinbeProvider.from(parsedUri).get

    val p = provider

    p.jinbeInjector = jinbeInjector

    p.get
  }
}
