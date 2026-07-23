/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
 * Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package play.modules.jinbe

import java.net.URI

import play.api.{ ApplicationLoader, BuiltInComponentsFromContext }
import play.api.inject.{ Injector, SimpleInjector }
import play.api.libs.ws.ahc.StandaloneAhcWSClient

/**
 * Can be used for a custom application loader.
 *
 * {{{
 * import play.api.ApplicationLoader
 * import play.modules.jinbe.JinbeFromContext
 *
 * class MyApplicationLoader extends ApplicationLoader {
 *   def load(context: ApplicationLoader.Context) =
 *     new MyComponents(context).application
 * }
 *
 * class MyComponents(context: ApplicationLoader.Context)
 *     extends JinbeFromContext(context) {
 *   lazy val router = play.api.routing.Router.empty
 *   lazy val httpFilters = Seq.empty[play.api.mvc.EssentialFilter]
 * }
 * }}}
 *
 * @param context the application loader context
 * @param name the name of the Jinbe configuration to be used
 */
abstract class JinbeFromContext(
    context: ApplicationLoader.Context,
    val name: String)
    extends BuiltInComponentsFromContext(context)
    with JinbeComponentsWithInjector {

  /**
   * Initializes Jinbe components from context using the default configuration.
   */
  def this(context: ApplicationLoader.Context) = this(context, "default")

  /**
   * Default implements just returns the initial injector.
   * Overrides this one to be able to pimp the injector.
   */
  protected def configureJinbe(initialInjector: Injector): Injector = {
    val i = new SimpleInjector(initialInjector)

    i + StandaloneAhcWSClient()
  }

  final def jinbeInjector: io.github.cchantep.jinbe.spi.Injector =
    new PlayInjector(configureJinbe(injector))

  private lazy val parsed: Option[URI] =
    JinbeConfig.parse(configuration).collectFirst { case (`name`, uri) => uri }

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  lazy val parsedUri: URI = parsed.getOrElse(
    throw configuration.globalError(s"Missing Jinbe configuration for '$name'")
  )
}
