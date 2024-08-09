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
  def getHourlyForecast(lat: String, long: String): F[Forecast]
}

class WeatherGovApi[F[_]: Async](client: Client[F]) extends Weather[F] {
  private def getPoints(lat: String, long: String): F[Points] = {
    val pointsTarget = uri"https://api.weather.gov/" / "points" / s"$lat,$long"
    client.expect(pointsTarget)(jsonOf[F, Points]).handleErrorWith {
      case e if e.getMessage.contains("301 Moved Permanently for request") => Async[F].raiseError(CoordinateErrors.CoordinatesTooLong("The coordinates provided are too long. Please limit decimal place to 4 digits and try again."))
      case e if e.getMessage.contains("404 Not Found for request") => Async[F].raiseError(CoordinateErrors.CoordinatesNotFound("Unable to get location for the coordinates provided. Please try again."))
      case e => Async[F].raiseError(e)
    }
  }

  override def getHourlyForecast(lat: String, long: String): F[Forecast] = {
    for {
      points <- getPoints(lat, long)
      forecastTarget = uri"https://api.weather.gov/" / "gridpoints" / points.gridId / s"${points.gridX},${points.gridY}" / "forecast" / "hourly"
      forecast <- client.expect(forecastTarget)(jsonOf[F, Forecast])
    } yield forecast
  }
}