package forecastr.routes

import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import forecastr.http.MockWeather
import org.http4s.*
import org.http4s.Status.InternalServerError
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
        .run(Request(Method.GET, Uri.unsafeFromString("/forecast/123/123")))
        .flatMap(_.body.compile.toVector).asserting { s =>
        new String(s.toArray, StandardCharsets.UTF_8) shouldBe """{"classification":"cold","shortForecast":"It's still sunny"}"""
      }
    }
    "Responds with a server error if weather can't be found" in {
      val mockWeather = new MockWeather(true)
      val routes = new ForecastRoute[IO](mockWeather)
      routes
        .foreCastRoutes
        .orNotFound
        .run(Request(Method.GET, Uri.unsafeFromString("/forecast/123/123")))
        .asserting {
          _.status shouldBe InternalServerError
        }
    }
  }
}