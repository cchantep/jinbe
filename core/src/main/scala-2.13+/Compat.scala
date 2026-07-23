/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
 * Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe

private[jinbe] object Compat {
  type Factory[M[_], T] = scala.collection.Factory[T, M[T]]

  @inline def newBuilder[M[_], T](f: Factory[M, T]) = f.newBuilder

  @inline def mapValues[K, V1, V2](m: Map[K, V1])(f: V1 => V2) =
    m.view.mapValues(f).toMap

  val javaConverters = scala.jdk.CollectionConverters
}
