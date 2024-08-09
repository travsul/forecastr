package forecastr.routes

import org.http4s.HttpRoutes
import cats.effect.*
import forecastr.http.Weather
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import cats.implicits.*
import forecastr.domain.*
import forecastr.logic.Output
import org.http4s.circe.*
import io.circe.*
import io.circe.syntax.*
import io.circe.generic.auto.deriveEncoder

class ForecastRoute[F[_]: Async](weather: Weather[F]) extends Http4sDsl[F] {
  private def formatOutput(outputMaybeForecast: Option[OutputForecast]): Json = {
    outputMaybeForecast match {
      case Some(forecast) => forecast.asJson
      case None => "No forecast found".asJson
    }
  }
  val foreCastRoutes = HttpRoutes.of[F] {
    case GET -> Root / "forecast" / lat / long =>
      (for {
        hourlyForecast <- weather.getHourlyForecast(lat, long)
        resp <- Ok(formatOutput(Output.getOutputForecastFromInput(hourlyForecast)))
      } yield resp).handleErrorWith {
        case e: CoordinateErrors.CoordinatesNotFound =>  NotFound(e.asJson)
        case e: CoordinateErrors.CoordinatesTooLong => BadRequest(e.asJson)
        case e => InternalServerError()  
      }
  }
}