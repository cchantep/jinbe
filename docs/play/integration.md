# Play Framework Integration

This documentation covers using Jinbe with [Play Framework](https://playframework.com/).

## Adding dependencies

Jinbe supports Play Framework 2.6 and later. Add the integration module and your chosen storage backend to `build.sbt`:

```scala
// Select your Play version
libraryDependencies ++= Seq(
  "io.github.cchantep" %% "jinbe-play" % "{{site.latest_release}}-play26" // Play 2.6.x
  // "io.github.cchantep" %% "jinbe-play" % "{{site.latest_release}}-play27" // Play 2.7.x
  // "io.github.cchantep" %% "jinbe-play" % "{{site.latest_release}}-play28" // Play 2.8.x
)

// Add your storage backend (example: S3)
val jinbeVer = "{{site.latest_release}}"
libraryDependencies += "io.github.cchantep" %% "jinbe-s3" % jinbeVer
```

## Module setup

Enable the Jinbe module in `application.conf`:

```conf
play.modules.enabled += "play.modules.jinbe.JinbeModule"
```

Then inject `ObjectStorage` into your controllers:

```scala
import javax.inject.Inject

import play.api.mvc.{ AbstractController, ControllerComponents }

import io.github.cchantep.jinbe.ObjectStorage

class MyController @Inject() (
  components: ControllerComponents,
  storage: ObjectStorage
) extends AbstractController(components) {
  // use storage ...
}
```

## Compile-time dependency injection (optional)

For compile-time dependency injection, use the `JinbeComponents` trait or `JinbeFromContext` helper class:

```scala
import play.api.ApplicationLoader

import io.github.cchantep.jinbe.ObjectStorage

import play.modules.jinbe._

class MyComponent1(
  context: ApplicationLoader.Context,
  name: String // Storage config name (see Configuration section)
) extends JinbeFromContext(context, name) {
  def httpFilters: Seq[play.api.mvc.EssentialFilter] = ???
  def router: play.api.routing.Router = ???
}

val storage: ObjectStorage = MyComponent1(...).jinbe

For more complex scenarios with multiple Play traits, use `JinbeComponentsWithInjector`:

In your Play application, you can use Jinbe with multiple storage backends (possibly with different kinds of storage and/or account), using the `@NamedStorage` annotation.

Consider the following configuration, with several storage URIs.

```
# The default URI
jinbe.uri = "s3:https://..."

# Another one, named with 'bar'
jinbe.bar.uri = "vfs:tmp:///"
```

Then the dependency injection can select the instances using the names.

```scala
import javax.inject.Inject

import io.github.cchantep.jinbe.ObjectStorage

import play.modules.jinbe.NamedStorage

class MyComponent @Inject() (
  val defaultStorage: ObjectStorage, // corresponds to 'jinbe.uri'
  @NamedStorage("bar") val barStorage: ObjectStorage // 'jinbe.bar'
) {

}
```

### Configuring storage

The module reads connection properties from `application.conf` using URI syntax:

```
jinbe.uri = "s3:https://..."
```

Each storage module provide a scheme support (e.g. `s3:`, `vfs:`, ...; See modules documentation).

This is especially helpful on platforms like Heroku, where the add-ons publish the storage URI in a single environment variable (e.g. `STORAGE_URI`).

```
jinbe.uri = ${?STORAGE_URI}
```

To configure a storage instance different from the default one (corresponding the `@NamedStorage("ANY_NAME")` annotation), use `jinbe.ANY_NAME.uri`:

```conf
jinbe.ANY_NAME.uri = "..."
```

> The Google Cloud Storage and S3 modules require a `StandaloneWSClient` in your Play context.

## Examples

*See the [examples](https://github.com/cchantep/jinbe/tree/master/examples) directory*
