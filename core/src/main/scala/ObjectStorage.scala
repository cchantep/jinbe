/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
 * Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe

import scala.concurrent.{ ExecutionContext, Future }

import akka.NotUsed

import akka.stream.Materializer
import akka.stream.scaladsl.{ Sink, Source }

/**
 * Root of the DSL.
 *
 * @define bucketNameParam the name of the bucket
 */
trait ObjectStorage { self =>

  /** Storage logger */
  private[jinbe] val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  /**
   * Returns a storage instance with specified request timeout.
   *
   * @param timeout the request timeout in milliseconds
   */
  def withRequestTimeout(timeout: Long): ObjectStorage

  /**
   * A request to list the buckets.
   */
  trait BucketsRequest {

    /**
     * Lists of all objects within the bucket.
     */
    def apply(
      )(implicit
        m: Materializer
      ): Source[Bucket, NotUsed]

    /**
     * Collects the bucket objects.
     */
    def collect[M[_]](
      )(implicit
        m: Materializer,
        @deprecatedName(Symbol("builder")) factory: Compat.Factory[M, Bucket]
      ): Future[M[Bucket]] = {
      implicit def ec: ExecutionContext = m.executionContext

      apply() runWith Sink
        .fold(Compat.newBuilder[M, Bucket](factory)) {
          _ += (_: Bucket)
        }
        .mapMaterializedValue(_.map(_.result()))
    }
  }

  /**
   * Prepares the request to list the buckets.
   *
   * {{{
   * import akka.stream.Materializer
   * import io.github.cchantep.jinbe.ObjectStorage
   *
   * def enumBuckets(store: ObjectStorage)(implicit m: Materializer) =
   *   store.buckets()
   *
   * def bucketSet(store: ObjectStorage)(implicit m: Materializer) =
   *   store.buckets.collect[Set]()
   * }}}
   */
  def buckets: BucketsRequest

  /**
   * Returns a reference to a bucket specified by its name.
   *
   * @param name $bucketNameParam
   */
  def bucket(name: String): BucketRef
}
