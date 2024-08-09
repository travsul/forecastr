package forecastr.http

import cats.effect.Async
import org.http4s.client.Client
import org.http4s.implicits.*
import forecastr.domain.*
import org.http4s.circe.*
import cats.implicits.*
import org.http4s.*

trait Weather[F[_]] {
  def getHourlyForecast(lat: String, long: String): F[Forecast]
}

class WeatherGovApi[F[_]: Async](client: Client[F]) extends Weather[F] {
  private def getPoints(lat: String, long: String): F[Points] = {
    val pointsTarget = uri"https://api.weather.gov/" / "points" / s"$lat,$long"
    client.expect(pointsTarget)(jsonOf[F, Points])
  }

  override def getHourlyForecast(lat: String, long: String): F[Forecast] = {
    for {
      points <- getPoints(lat, long)
      forecastTarget = uri"https://api.weather.gov/" / "gridpoints" / points.gridId / s"${points.gridX},${points.gridY}" / "forecast" / "hourly"
      forecast <- client.expect(forecastTarget)(jsonOf[F, Forecast])
    } yield forecast
  }
}