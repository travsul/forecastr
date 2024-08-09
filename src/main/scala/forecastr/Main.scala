package forecastr

import cats.effect.*
import org.http4s.ember.client.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import com.comcast.ip4s.*
import forecastr.http.WeatherGovApi
import forecastr.routes.ForecastRoute
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory.create[IO]
    (for {
      client <- EmberClientBuilder.default[IO].build
      weather = new WeatherGovApi[IO](client)
      server <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(ForecastRoute[IO](weather).foreCastRoutes.orNotFound)
        .build
    } yield server)
      .useForever
      .map(_ => ExitCode.Success)
  }
}