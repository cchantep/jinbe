/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe.vfs

import org.apache.commons.vfs2.{
  FileName,
  FileSelectInfo,
  FileType,
  FileTypeSelector
}

private[vfs] class JinbeFileSelector(
    parent: FileName,
    fileType: FileType,
    prefix: Option[String])
    extends FileTypeSelector(fileType) {

  private lazy val parentNameSz = parent.getPath.size + 1

  override def includeFile(fileInfo: FileSelectInfo): Boolean =
    super.includeFile(fileInfo) && {
      val fname = fileInfo.getFile.getName
      def relativeName = fname.getPath.drop(parentNameSz)

      prefix.forall(relativeName.startsWith) && fname.getExtension != "metadata"
    }
}
