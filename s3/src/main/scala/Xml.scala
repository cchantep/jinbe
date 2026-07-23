/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe.s3

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.xml.Node

import io.github.cchantep.jinbe.{ Bytes, Object, VersionedObject }

// !! UNSAFE - Can raise exceptions
private[s3] object Xml {

  def objectFromXml(content: Node): Object =
    Object(
      name = (content \ "Key").text,
      size = size(content),
      lastModifiedAt = lastModified(content)
    )

  def versionDecoder(content: Node): VersionedObject = {
    VersionedObject(
      name = (content \ "Key").text,
      size = size(content),
      versionCreatedAt = lastModified(content),
      versionId = (content \ "VersionId").text,
      isLatest = (content \ "IsLatest").text.toBoolean
    )
  }

  def deleteMarkerDecoder(content: Node): VersionedObject = {
    VersionedObject(
      name = (content \ "Key").text,
      size = Bytes(-1),
      versionCreatedAt = lastModified(content),
      versionId = (content \ "VersionId").text,
      isLatest = (content \ "IsLatest").text.toBoolean
    )
  }

  // ---

  @inline private def size(content: Node): Bytes =
    Bytes((content \ "Size").text.toLong)

  @inline private def lastModified(content: Node): LocalDateTime =
    LocalDateTime.parse(
      (content \ "LastModified").text,
      DateTimeFormatter.ISO_OFFSET_DATE_TIME
    )
}
