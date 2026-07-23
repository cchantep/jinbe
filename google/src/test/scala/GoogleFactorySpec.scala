package tests.jinbe.google

import java.net.URI

import io.github.cchantep.jinbe.google.{
  GoogleFactory,
  GoogleScheme,
  GoogleStorage
}
import io.github.cchantep.jinbe.google.tests.TestUtils
import io.github.cchantep.jinbe.spi.{ Injector, Registry, StorageScheme }

final class GoogleFactorySpec extends org.specs2.mutable.Specification {
  "Google factory".title

  "Google storage" should {
    val loader = java.util.ServiceLoader.load(classOf[StorageScheme])
    lazy val scheme: StorageScheme = {
      def go(it: java.util.Iterator[StorageScheme]): StorageScheme = {
        if (!it.hasNext) {
          null
        } else {
          val s = it.next()

          if (s.scheme == "google") {
            s
          } else {
            go(it)
          }
        }
      }

      go(loader.iterator)
    }

    def factory = scheme.factoryClass.getDeclaredConstructor().newInstance()

    {
      val uri = new URI(TestUtils.configUri)

      s"be resolved from ${uri.toString}" in {
        scheme must beAnInstanceOf[GoogleScheme] and {
          factory(WSInjector, uri) must beAnInstanceOf[GoogleStorage]
        }
      }
    }

    {
      val uri = new URI("foo:google")

      s"not be resolved from ${uri.toString}" in {
        factory(WSInjector, uri) must throwA[Exception](
          "Expected URI with scheme.*"
        )
      }
    }
  }

  "Registry" should {
    val reg = Registry.getInstance
    val scheme = "google"

    "find the registered schemes" in {
      reg.schemes must contain(atLeast(scheme))
    }

    s"resolve the factory for $scheme" in {
      reg.factoryClass(scheme) must beSome(classOf[GoogleFactory])
    }
  }

  // ---

  import play.api.libs.ws.ahc.StandaloneAhcWSClient

  private implicit def materializer: akka.stream.Materializer =
    TestUtils.materializer

  object WSInjector extends Injector {
    private val WS = classOf[StandaloneAhcWSClient]

    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    def instanceOf[T](cls: Class[T]): T = cls match {
      case WS => StandaloneAhcWSClient().asInstanceOf[T]
      case _  => ???
    }
  }
}
