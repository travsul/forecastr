package forecastr.http

import cats.effect.Async
import org.http4s.client.Client
import org.http4s.implicits.*
import forecastr.domain.*
import org.http4s.circe.*
import cats.implicits.*
import io.circe.ParsingFailure
import org.http4s.*

trait Weather[F[_]] {
  def getHourlyForecast(latLong: Coordinates): F[Forecast]
}

class WeatherGovApi[F[_]: Async](client: Client[F]) extends Weather[F] {
  private def getPoints(latLong: Coordinates): F[Points] = {
    val pointsTarget = uri"https://api.weather.gov/" / "points" / s"${latLong.lat},${latLong.long}"
    client.expect(pointsTarget)(jsonOf[F, Points]).handleErrorWith {
      case e if e.getMessage.contains("404 Not Found for request") => Async[F].raiseError(ForecastErrors.CoordinatesNotFound("Unable to get location for the coordinates provided."))
      case e => Async[F].raiseError(e)
    }
  }

  override def getHourlyForecast(latLong: Coordinates): F[Forecast] = {
    for {
      points <- getPoints(latLong)
      forecastTarget = uri"https://api.weather.gov/" / "gridpoints" / points.gridId / s"${points.gridX},${points.gridY}" / "forecast" / "hourly"
      forecast <- client.expect(forecastTarget)(jsonOf[F, Forecast])
    } yield forecast
  }
}