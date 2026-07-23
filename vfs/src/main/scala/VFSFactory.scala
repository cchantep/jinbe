/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
 * Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe.vfs

import java.net.URI

import io.github.cchantep.jinbe.ObjectStorage
import io.github.cchantep.jinbe.spi.{ Injector, StorageFactory, StorageScheme }

final class VFSFactory extends StorageFactory {

  @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
  def apply(injector: Injector, uri: URI): ObjectStorage =
    VFSStorage(VFSTransport[URI](uri).get)
}

/** Storage scheme for VFS */
final class VFSScheme extends StorageScheme {
  val scheme = "vfs"

  @inline
  def factoryClass: Class[_ <: StorageFactory] = classOf[VFSFactory]
}
