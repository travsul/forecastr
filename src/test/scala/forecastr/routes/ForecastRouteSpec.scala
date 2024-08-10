package forecastr.routes

import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import forecastr.http.MockWeather
import org.http4s.*
import org.http4s.Status.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AsyncFreeSpec

import java.nio.charset.StandardCharsets

class ForecastRouteSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  "ForecastRoute " - {
    "Responds with a great weather when one is found" in {
      val mockWeather = new MockWeather()
      val routes = new ForecastRoute[IO](mockWeather)
      routes
        .foreCastRoutes
        .orNotFound
        .run(Request(Method.GET, Uri.unsafeFromString("/forecast/73.123/73.123")))
        .flatMap(_.body.compile.toVector).asserting { s =>
        new String(s.toArray, StandardCharsets.UTF_8) shouldBe """{"classification":"cold","shortForecast":"It's still sunny"}"""
      }
    }
    "Responds with a InternalServerError if weather can't be found" in {
      val mockWeather = new MockWeather(true)
      val routes = new ForecastRoute[IO](mockWeather)
      routes
        .foreCastRoutes
        .orNotFound
        .run(Request(Method.GET, Uri.unsafeFromString("/forecast/73.123/73.123")))
        .asserting {
          _.status shouldBe InternalServerError
        }
    }
    "Responds with a BadRequest if longer than 4 decimal places" in {
      val mockWeather = new MockWeather()
      val routes = new ForecastRoute[IO](mockWeather)
      routes
        .foreCastRoutes
        .orNotFound
        .run(Request(Method.GET, Uri.unsafeFromString("/forecast/73.123456/73.123456")))
        .asserting {
          _.status shouldBe BadRequest
        }
    }
    "Responds with a BadRequest if given nonsense strings instead of coordinates" in {
      val mockWeather = new MockWeather()
      val routes = new ForecastRoute[IO](mockWeather)
      routes
        .foreCastRoutes
        .orNotFound
        .run(Request(Method.GET, Uri.unsafeFromString("/forecast/brucewayneis/batman")))
        .asserting {
          _.status shouldBe BadRequest
        }
    }
  }
}