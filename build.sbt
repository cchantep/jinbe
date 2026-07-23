ThisBuild / organization := "io.github.cchantep"

ThisBuild / scalaVersion := "2.12.21"

ThisBuild / crossScalaVersions := Seq(
  "2.11.12",
  scalaVersion.value,
  "2.13.18",
  "3.4.3"
)

lazy val core = project
  .in(file("core"))
  .settings(
    name := "jinbe-core",
    Compile / compile / scalacOptions ++= {
      if (scalaBinaryVersion.value.startsWith("3")) {
        Seq("-language:higherKinds")
      } else if (scalaBinaryVersion.value == "2.12") {
        Seq("-language:higherKinds")
      } else {
        Seq(
          // Silencer
          "-P:silencer:globalFilters=constructor\\ deprecatedName\\ in\\ class\\ deprecatedName\\ is\\ deprecated;outer\\ reference.*cannot\\ be\\ checked",
          "-language:higherKinds"
        )
      }
    },
    Compile / compile / scalacOptions ++= {
      if (scalaBinaryVersion.value == "2.13") {
        Seq("-Wconf:cat=deprecation&msg=.*deprecatedName.*:s")
      } else {
        Seq.empty
      }
    },
    mimaBinaryIssueFilters ++= {
      import com.typesafe.tools.mima.core._, ProblemFilters.{ exclude => x }

      Seq(
        x[ReversedMissingMethodProblem](
          "io.github.cchantep.jinbe.BucketRef#ListRequest.withPrefix"
        ),
        x[ReversedMissingMethodProblem]("io.github.cchantep.jinbe.BucketVersioning#VersionedListRequest.withPrefix"),
        x[FinalClassProblem]("io.github.cchantep.jinbe.Bucket"),
        x[FinalClassProblem]("io.github.cchantep.jinbe.ByteRange"),
        x[FinalClassProblem]("io.github.cchantep.jinbe.Object"),
        x[FinalClassProblem]("io.github.cchantep.jinbe.VersionedObject"),
        x[FinalClassProblem](
          "io.github.cchantep.jinbe.exception.JinbeUnknownError"
        ),
        x[FinalClassProblem](
          "io.github.cchantep.jinbe.exception.BucketAlreadyExistsException"
        ),
        x[FinalClassProblem](
          "io.github.cchantep.jinbe.exception.BucketNotEmptyException"
        ),
        x[FinalClassProblem](
          "io.github.cchantep.jinbe.exception.BucketNotFoundException"
        ),
        x[FinalClassProblem](
          "io.github.cchantep.jinbe.exception.ObjectNotFoundException"
        ),
        x[FinalClassProblem](
          "io.github.cchantep.jinbe.exception.VersionNotFoundException"
        )
      )
    },
    libraryDependencies ++= Seq(
      "commons-codec" % "commons-codec" % "1.22.0",
      Dependencies.slf4jApi % Provided
    ),
    libraryDependencies ~= {
      _.map(_.exclude("com.typesafe", "ssl-config-core"))
    }
  )

val scalaXmlVer = Def.setting[String] {
  val sv = scalaBinaryVersion.value

  if (sv.startsWith("3")) "2.0.1"
  else if (sv == "2.13") "1.3.0"
  else "1.0.6"
}

import Dependencies.Version.{ play => playVer, playJson => playJsonVer }

lazy val s3 = project
  .in(file("s3"))
  .settings(
    name := "jinbe-s3",
    Compile / compile / scalacOptions ++= {
      if (scalaBinaryVersion.value == "2.11") {
        Seq(
          "-P:silencer:globalFilters=outer\\ reference.*cannot\\ be\\ checked;constructor\\ URL\\ in\\ class\\ URL\\ is\\ deprecated",
          "-language:higherKinds"
        )
      } else if (scalaBinaryVersion.value == "2.13") {
        Seq(
          "-Wconf:cat=unchecked&msg=outer\\ reference.*cannot\\ be\\ checked:s"
        )
      } else {
        Seq.empty
      }
    },
    libraryDependencies ++= Dependencies.playAhcWS.value ++ Seq(
      Dependencies.playWSXml.value,
      "org.scala-lang.modules" %% "scala-xml" % scalaXmlVer.value % Provided
    )
  )
  .dependsOn(core % "test->test;compile->compile")

lazy val google = project
  .in(file("google"))
  .settings(
    name := "jinbe-google",
    Compile / compile / scalacOptions ++= {
      if (scalaBinaryVersion.value == "2.13") {
        Seq(
          "-Wconf:cat=unchecked&msg=outer\\ reference.*cannot\\ be\\ checked:s"
        )
      } else {
        Seq.empty
      }
    },
    libraryDependencies ++= Dependencies.playAhcWS.value ++ Seq(
      "com.typesafe.play" %% "play-json" % Dependencies.Version.playJson.value,
      Dependencies.playWSJson.value,
      "com.google.auth" % "google-auth-library-oauth2-http" % "1.49.0",
      "com.google.apis" % "google-api-services-storage" % "v1-rev20210127-1.31.0"
    )
  )
  .dependsOn(core % "test->test;compile->compile")

lazy val vfs = project
  .in(file("vfs"))
  .settings(
    name := "jinbe-vfs",
    Compile / compile / scalacOptions ++= {
      if (scalaBinaryVersion.value == "2.13") {
        Seq(
          "-Wconf:cat=unchecked&msg=outer\\ reference.*cannot\\ be\\ checked:s"
        )
      } else {
        Seq.empty
      }
    },
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-vfs2" % "2.10.0",
      "com.typesafe.play" %% "play-json" % playJsonVer.value,
      Dependencies.slf4jApi,
      "commons-io" % "commons-io" % "2.22.0" % Test
    )
  )
  .dependsOn(core % "test->test;compile->compile")

lazy val gridfs = project
  .in(file("gridfs"))
  .settings(
    name := "jinbe-gridfs",
    Compile / compile / scalacOptions ++= {
      if (scalaBinaryVersion.value == "2.13") {
        Seq(
          "-Wconf:cat=unchecked&msg=outer\\ reference.*cannot\\ be\\ checked:s"
        )
      } else {
        Seq.empty
      }
    },
    libraryDependencies ++= Seq(
      "org.reactivemongo" %% "reactivemongo" % "1.1.0-RC20",
      Dependencies.slf4jApi
    )
  )
  .dependsOn(core % "test->test;compile->compile")

lazy val playTest = Def.setting {
  val ver = {
    if (scalaBinaryVersion.value.startsWith("3")) {
      "2.8.11"
    } else if (
      scalaBinaryVersion.value == "2.13" && playVer.value.startsWith("2.7")
    ) {
      "2.8.11"
    } else {
      playVer.value
    }
  }

  ("com.typesafe.play" %% "play-test" % ver)
    .cross(CrossVersion.for3Use2_13) // TODO: Remove
    .exclude("com.typesafe.play", "*")
    .exclude("org.scala-lang.modules", "*")
    .exclude("com.typesafe.akka", "*") % Test
}

lazy val play = project
  .in(file("play"))
  .settings(
    name := "jinbe-play",
    version := {
      val ver = (ThisBuild / version).value
      val playMajor = playVer.value.split("\\.").take(2).mkString

      if (ver.endsWith("-SNAPSHOT")) {
        s"${ver.dropRight(9)}-play${playMajor}-SNAPSHOT"
      } else {
        s"${ver}-play${playMajor}"
      }
    },
    Test / sourceDirectory := {
      if (scalaBinaryVersion.value.startsWith("3")) {
        baseDirectory.value / "no-test" // TODO
      } else {
        (Test / sourceDirectory).value
      }
    },
    Test / unmanagedSourceDirectories ++= {
      if (scalaBinaryVersion.value.startsWith("3")) {
        Seq.empty
      } else {
        val v = playVer.value.split("\\.").take(2).mkString(".")

        Seq(baseDirectory.value / "src" / "test" / s"play-$v")
      }
    },
    libraryDependencies ++= {
      val exclude: ModuleID => ModuleID = {
        if (scalaBinaryVersion.value.startsWith("3")) { (mid: ModuleID) =>
          mid
            .exclude("com.typesafe.akka", "*")
            .exclude("org.scala-lang.modules", "*")
            .exclude("com.typesafe.play", "play-json_2.13")

        } else { (mid: ModuleID) =>
          mid
            .exclude("com.typesafe.akka", "*")
            .exclude("org.scala-lang.modules", "*")
        }
      }

      val playMain = exclude(
        (
          if (
            scalaBinaryVersion.value == "2.13" && playVer.value
              .startsWith("2.7")
          ) {
            "com.typesafe.play" %% "play" % "2.8.11"
          } else {
            "com.typesafe.play" %% "play" % playVer.value
          }
        ).cross(CrossVersion.for3Use2_13)
      ) % Provided

      val playDeps: Seq[ModuleID] = {
        if (playVer.value.startsWith("2.6")) {
          Seq(playMain)
        } else if (
          scalaBinaryVersion.value == "2.13" && playVer.value.startsWith("2.7")
        ) {
          Seq(
            playMain,
            "com.typesafe.play" % "play-exceptions" % "2.8.11" % Provided
          )
        } else {
          Seq(
            playMain,
            "com.typesafe.play" % "play-exceptions" % playVer.value % Provided
          )
        }
      }

      Dependencies.playAhcWS.value ++ playDeps ++ Seq(
        playTest.value,
        (
          if (
            scalaBinaryVersion.value == "2.13" && playVer.value
              .startsWith("2.7")
          ) {
            ("com.typesafe.play" %% "play-guice" % "2.8.11")
              .cross(CrossVersion.for3Use2_13)
          } else {
            ("com.typesafe.play" %% "play-guice" % playVer.value)
              .cross(CrossVersion.for3Use2_13)
          }
        ).exclude("com.typesafe.play", "*") % Test,
        "com.typesafe.akka" %% "akka-actor-typed" % Dependencies.Version.akka.value % Provided
      )
    },
    Compile / doc / sources := {
      val compiled = (Compile / sources).value

      compiled.filter { _.getName.endsWith("NamedStorage.java") }
    }
  )
  .dependsOn(core % "test->test;compile->compile", vfs % "test->compile")

lazy val jinbe = project
  .in(file("."))
  .enablePlugins(ScalaUnidocPlugin)
  .settings(
    Publish.settings ++ Seq(
      libraryDependencies ++= Dependencies.playAhcWS.value,
      pomPostProcess := Common.transformPomDependencies { depSpec =>
        // Filter in POM the dependencies only required to compile sample in doc

        if ((depSpec \ "artifactId").text.startsWith("jinbe-")) {
          Some(depSpec)
        } else {
          Option.empty
        }
      },
      mimaPreviousArtifacts := Set(
        /* organization.value %% name.value % previousRelease */
      ),
      doc / excludeFilter := "play",
      ScalaUnidoc / unidoc / unidocAllSources ~= {
        _.map(_.filterNot { f =>
          val name = f.getName

          name.startsWith("NamedStorage") ||
          name.indexOf("-md-") != -1 ||
          (name.startsWith("package") &&
            f.getPath.indexOf("src_managed") != -1)
        })
      }
    )
  )
  .dependsOn(s3, google, vfs, gridfs, play)
  .aggregate(core, s3, google, vfs, gridfs, play)
