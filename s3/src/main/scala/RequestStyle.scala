/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe.s3

/** S3 [[http://docs.aws.amazon.com/AmazonS3/latest/dev/VirtualHosting.html request style]]. */
// TODO: Move to separate file
private[s3] sealed trait RequestStyle
private[s3] object PathRequest extends RequestStyle
private[s3] object VirtualHostRequest extends RequestStyle

private[s3] object RequestStyle {

  def apply(raw: String): RequestStyle = raw match {
    case "virtualhost" => VirtualHostRequest
    case _             => PathRequest
  }
}
