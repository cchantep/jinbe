#! /bin/sh

set -e

S3="3.4"

# Mirror the CircleCI publish matrix from .circleci/config.yml.
#
# Play 2.10 / 3.0 are intentionally excluded for now: this build still relies on
# com.typesafe.play artifacts such as play-guice / play-ws-standalone, and local
# packaging checks fail to resolve those dependencies for the newer Play lines.

# Play 2.6.x
export PLAY_VERSION=2.6.7 PLAY_JSON_VERSION=2.6.7 WS_VERSION=1.1.6
sbt clean ++2.11 makePom packageBin packageSrc packageDoc \
    ++2.12 makePom packageBin packageSrc packageDoc

# Play 2.7.x
export PLAY_VERSION=2.7.1 PLAY_JSON_VERSION=2.9.1 WS_VERSION=2.0.6
sbt ++2.12 makePom packageBin packageSrc packageDoc \
    ++2.13 makePom packageBin packageSrc packageDoc

# Play 2.8.x
export PLAY_VERSION=2.8.0 PLAY_JSON_VERSION=2.8.1 WS_VERSION=2.1.2
sbt ++2.13 makePom packageBin packageSrc packageDoc

# Scala 3
export PLAY_VERSION=2.9.5 PLAY_JSON_VERSION=2.10.6 WS_VERSION=2.2.11
sbt ++${S3} makePom packageBin packageSrc packageDoc

# GridFS (tested on Scala 2.12/2.13)
export PLAY_VERSION=2.7.1 PLAY_JSON_VERSION=2.9.1 WS_VERSION=2.0.6
sbt ++2.12 gridfs/makePom gridfs/packageBin gridfs/packageSrc gridfs/packageDoc
sbt ++2.13 gridfs/makePom gridfs/packageBin gridfs/packageSrc gridfs/packageDoc
