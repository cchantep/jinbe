import sbt._
import sbt.Keys._

import xsbti.HashedVirtualFileRef

import sbtheader.HeaderPlugin.autoImport._

import com.typesafe.tools.mima.plugin.MimaKeys.{
  mimaFailOnNoPrevious,
  mimaPreviousArtifacts
}

object Publish {
  val siteUrl = "https://cchantep.github.io/jinbe/"

  @inline def env(n: String): String = sys.env.getOrElse(n, n)

  lazy val repoName = env("PUBLISH_REPO_NAME")
  lazy val repoUrl = env("PUBLISH_REPO_URL")

  lazy val settings = Seq(
    Compile / packageBin / mappings ~= coreFilter,
    Compile / packageSrc / mappings ~= coreFilter,
    licenses := Seq(License.Apache2),
    pomIncludeRepository := { _ => false },
    autoAPIMappings := true,
    apiURL := Some(url(siteUrl)), // TODO
    homepage := Some(url(siteUrl)),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/cchantep/jinbe"),
        "git@github.com:cchantep/jinbe.git"
      )
    ),
    headerLicense := {
      val currentYear = java.time.Year.now(java.time.Clock.systemUTC).getValue
      Some(HeaderLicense.Custom(s"Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>\nCopyright (C) $currentYear Cédric Chantepie <https://github.com/cchantep>"))
    },
    developers := List(
      Developer(
        id = "cchantep",
        name = "Cédric Chantepie",
        email = "",
        url = url("http://github.com/cchantep/")
      )
    ),
    publishTo := Some(repoUrl).map(repoName at _),
    credentials += Credentials(
      repoName,
      env("PUBLISH_REPO_ID"),
      env("PUBLISH_USER"),
      env("PUBLISH_PASS")
    )
  )

  private lazy val coreFilter: Seq[(HashedVirtualFileRef, String)] => Seq[(HashedVirtualFileRef, String)] = {
    (_: Seq[(HashedVirtualFileRef, String)]).filter {
      case (file, name) =>
        name.indexOf("com/github/ghik/silencer") == -1
    }
  }
}
