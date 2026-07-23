/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
 * Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe.exception

import io.github.cchantep.jinbe.{ BucketRef, ObjectRef, VersionedObjectRef }

/** An error when either a bucket or an object is not found. */
abstract class EntityNotFoundException extends JinbeException

/** An error when a bucket is not found. */
final case class BucketNotFoundException(
    bucketName: String)
    extends EntityNotFoundException {
  override def getMessage: String = s"Bucket '$bucketName' not found."
}

object BucketNotFoundException {

  private[jinbe] def apply(bucket: BucketRef): BucketNotFoundException =
    BucketNotFoundException(bucket.name)
}

/** An error when an object is not found. */
final case class ObjectNotFoundException(
    bucketName: String,
    objectName: String)
    extends EntityNotFoundException {

  override def getMessage: String =
    s"Object '$objectName' not found inside bucket '$bucketName'."
}

object ObjectNotFoundException {

  private[jinbe] def apply(obj: ObjectRef): ObjectNotFoundException =
    ObjectNotFoundException(obj.bucket, obj.name)
}

/** An error when an object version is not found. */
final case class VersionNotFoundException(
    bucketName: String,
    objectName: String,
    versionId: String)
    extends EntityNotFoundException {

  override def getMessage: String =
    s"Version '$versionId' of object '$objectName' not found inside bucket '$bucketName'."
}

object VersionNotFoundException {

  private[jinbe] def apply(
      version: VersionedObjectRef
    ): VersionNotFoundException =
    VersionNotFoundException(version.bucket, version.name, version.versionId)
}
