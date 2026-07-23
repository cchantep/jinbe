package io.github.cchantep.jinbe.tests

import io.github.cchantep.jinbe.{ BucketRef, ObjectRef, VersionedObjectRef }
import io.github.cchantep.jinbe.exception.{
  BucketAlreadyExistsException,
  BucketNotEmptyException,
  BucketNotFoundException,
  ObjectNotFoundException,
  VersionNotFoundException
}

object TestUtils {

  def bucketNotEmpty(bucket: BucketRef): BucketNotEmptyException =
    BucketNotEmptyException(bucket)

  def bucketAlreadyExists(bucket: BucketRef): BucketAlreadyExistsException =
    BucketAlreadyExistsException(bucket)

  def versionNotFound(ref: VersionedObjectRef): VersionNotFoundException =
    VersionNotFoundException(ref)

  def bucketNotFound(bucket: BucketRef): BucketNotFoundException =
    BucketNotFoundException(bucket)

  def objectNotFound(ref: ObjectRef): ObjectNotFoundException =
    ObjectNotFoundException(ref)
}
