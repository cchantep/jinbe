# Jinbe

A Scala framework for unified object storage access, supporting Amazon S3, Google Cloud Storage, and local/remote filesystems via Apache VFS.

## Build

The project is using [SBT](http://www.scala-sbt.org/), so to build it from sources the following command can be used.

```bash
./project/build.sh
```

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/cchantep/jinbe/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/cchantep/jinbe/tree/main)
[![Maven](https://img.shields.io/maven-central/v/io.github.cchantep/jinbe-core_2.13.svg)](https://central.sonatype.com/artifact/io.github.cchantep/jinbe-core_2.13/dependents)
[![Javadocs](https://javadoc.io/badge/io.github.cchantep/jinbe-core_2.12.svg)](https://javadoc.io/doc/io.github.cchantep/jinbe-core_2.13)

> The environment variable `PLAY_VERSION` can be set to build the `play` module appropriately.

## Getting Started

The simplest way to get started is with the [QuickStart guide](https://cchantep.github.io/jinbe/). Core concepts:

- All storage operations work through an `ObjectStorage` interface
- Buckets are accessed via `BucketRef` (for modification)
- Objects are accessed via `ObjectRef` (for read/write/delete)
- Operations are async with Akka Streams

**Available backends:**

- [S3](docs/s3/usage.md) — Amazon S3 and compatible services (CEPH, MinIO, etc.)
- [Google Cloud Storage](docs/google/usage.md) — Google's cloud storage service
- [Apache VFS](docs/vfs/usage.md) — Local and remote filesystems
- [MongoDB GridFS](docs/gridfs/usage.md) — MongoDB GridFS for distributed file storage

## Documentation

- [QuickStart](https://cchantep.github.io/jinbe/) — Introduction and core concepts
- [Examples](./examples) — Runnable example code
- [API Docs](https://cchantep.github.io/jinbe/api/) — Detailed API documentation
- [Play Integration](docs/play/integration.md) — Using Jinbe with Play Framework

## Publish release

To publish a release on Maven Central, use the following steps.

- Build artifacts: `./project/build.sh`
- Publish all modules: `./project/deploy.sh <version> <pgp-key>`
- Publish only play modules:

```bash
export SCALA_MODULES="play:jinbe-play"
./project/deploy.sh <version> <pgp-key>
```

- Go to https://oss.sonatype.org/#stagingRepositories and login with user allowed to publish on Maven central.

## Publish snapshot

Execute `./project/snapshot.sh`
