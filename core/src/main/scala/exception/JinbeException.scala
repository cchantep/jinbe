/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
 * Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe.exception

import java.io.IOException

import io.github.cchantep.jinbe.BucketRef

/** An error while using an object storage. */
abstract class JinbeException extends IOException

/** An unknown error */
final case class JinbeUnknownError(
    message: String,
    throwable: Option[Throwable] = None)
    extends JinbeException {

  override def getMessage: String =
    throwable.map(t => s"$message - ${t.getMessage}").getOrElse(message)
}

/**
 * An error when an operation cannot be applied on a not empty bucket
 * (e.g. delete).
 */
final case class BucketNotEmptyException(bucketName: String)
    extends JinbeException {
  override def getMessage: String = s"Bucket '$bucketName' was not empty."
}

object BucketNotEmptyException {

  private[jinbe] def apply(bucket: BucketRef): BucketNotEmptyException =
    BucketNotEmptyException(bucket.name)
}

/**
 * An error when an operation is refused for an already existing bucket.
 *
 * {{{
 * import scala.concurrent.ExecutionContext
 *
 * import io.github.cchantep.jinbe.ObjectStorage
 * import io.github.cchantep.jinbe.exception.BucketAlreadyExistsException
 *
 * def foo(
 *   storage: ObjectStorage,
 *   bucketName: String)(implicit ec: ExecutionContext) =
 *   storage.bucket(bucketName).create(failsIfExists = true).map { _ =>
 *     println(s"\$bucketName created")
 *   }.recover {
 *     case BucketAlreadyExistsException(_) =>
 *       println(s"\$bucketName already exists")
 *   }
 * }}}
 */
final case class BucketAlreadyExistsException(
    bucketName: String)
    extends JinbeException {
  override def getMessage: String = s"Bucket '$bucketName' already exists."
}

object BucketAlreadyExistsException {

  private[jinbe] def apply(bucket: BucketRef): BucketAlreadyExistsException =
    BucketAlreadyExistsException(bucket.name)
}
