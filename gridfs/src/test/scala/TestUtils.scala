/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
 * Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package tests.jinbe.gridfs

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem

import akka.stream.{ ActorMaterializer, Materializer }

import io.github.cchantep.jinbe.gridfs.{ GridFSFactory, GridFSStorage }

object TestUtils {
  private lazy val conf = ConfigFactory.load("tests.conf")

  private lazy val system = ActorSystem("GridFSTests")

  implicit def materializer: Materializer =
    ActorMaterializer()(system)

  private var storageOpt: Option[GridFSStorage] = None

  def gridfs: GridFSStorage = {
    storageOpt.getOrElse {
      val uri = conf.getString("mongodb.uri")
      val storage = GridFSFactory.create(uri).get

      storageOpt = Some(storage)

      storage
    }
  }

  def close(): Unit = {
    storageOpt.foreach { storage => storage.transport.close() }

    @SuppressWarnings(Array("org.wartremover.warts.UnusedMethodParameter"))
    val _ = system.terminate()
  }
}
