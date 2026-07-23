/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
 * Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package play.modules.jinbe

import java.net.URI

import scala.util.{ Failure, Success, Try }

import com.typesafe.config.ConfigValue

import play.api.Configuration

private[jinbe] object JinbeConfig {
  val logger = org.slf4j.LoggerFactory.getLogger(getClass)

  val registry: io.github.cchantep.jinbe.spi.Registry =
    io.github.cchantep.jinbe.spi.Registry.getInstance

  object ValidUri {

    @SuppressWarnings(
      Array(
        "org.wartremover.warts.AsInstanceOf",
        "org.wartremover.warts.IsInstanceOf",
        "org.wartremover.warts.ToString"
      )
    )
    def unapply(value: ConfigValue): Option[URI] = {
      if (!value.unwrapped.isInstanceOf[String]) {
        None
      } else {
        Try(value.unwrapped.asInstanceOf[String]).map(new URI(_)) match {
          case Failure(cause) => {
            logger.debug(s"Invalid URI: ${value.toString}", cause)

            Option.empty[URI]
          }

          case Success(uri) => {
            val s = uri.getScheme

            if (registry.schemes contains s) {
              Some(uri)
            } else {
              logger.debug(s"Unsupported scheme '$s': ${uri.toString}")

              Option.empty[URI]
            }
          }
        }
      }
    }
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def parse(configuration: Configuration): Set[(String, URI)] =
    configuration.getOptional[Configuration]("jinbe") match {
      case Some(subConf) => {
        val parsed = Set.newBuilder[(String, URI)]

        subConf.entrySet.iterator.foreach[Unit] {
          case ("uri", ValidUri(uri)) => {
            parsed += "default" -> uri

            ()
          }

          case ("uri", invalid) =>
            logger.warn(s"Invalid setting for 'jinbe.uri': ${invalid.toString}")

          case ("default.uri", ValidUri(uri)) => {
            parsed += "default" -> uri

            ()
          }

          case ("default.uri", invalid) =>
            logger.warn(
              s"Invalid setting for 'jinbe.default.uri': ${invalid.toString}"
            )

          case (key, ValidUri(uri)) if key.endsWith(".uri") => {
            parsed += key.dropRight(4) -> uri

            ()
          }

          case (key, invalid) =>
            logger.warn(s"Invalid setting for '$key': ${invalid.toString}")
        }

        val uris = parsed.result()

        if (uris.isEmpty) {
          logger.warn("No configuration in the 'jinbe' section")
        }

        uris
      }

      case _ => {
        logger.warn("No 'jinbe' section found in the configuration")

        Set.empty
      }
    }
}
