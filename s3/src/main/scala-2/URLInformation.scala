/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe.s3

import java.net.URL

/** Extractor for URL. */
private[s3] object URLInformation {

  /** Extracts (protocol scheme, host with port) from the given url. */
  def unapply(url: URL): Some[(String, String)] = {
    val hostAndPort: String = {
      if (url.getPort > 0) {
        s"${url.getHost}:${url.getPort.toString}"
      } else {
        url.getHost
      }
    }

    Some(url.getProtocol -> hostAndPort)
  }
}
