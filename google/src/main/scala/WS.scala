/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe.google

import akka.stream.Materializer

import play.api.libs.ws.ahc.{ AhcWSClientConfig, StandaloneAhcWSClient }

private[google] object WS {

  /** Returns a WS client (take care to close it once used). */
  def client(
      config: AhcWSClientConfig = AhcWSClientConfig()
    )(implicit
      materializer: Materializer
    ): StandaloneAhcWSClient = StandaloneAhcWSClient(config)
}
