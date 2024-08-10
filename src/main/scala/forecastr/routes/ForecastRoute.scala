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
  private def validateCoordinate(coord: String): Either[ForecastErrors.InvalidCoordinates, String] =
    Either.cond(
      coord.matches("""^-?([1-8]?[1-9]|[1-9]0)\.{1}\d{1,4}"""),
      coord,
      CoordinateErrors.InvalidCoordinates("Coordinates entered are invalid. Please make sure coordinates are valid and are kept to no more than 4 decimal places.")
    )

  private def formatOutput(outputMaybeForecast: Option[OutputForecast]): Json = {
    outputMaybeForecast match {
      case Some(forecast) => forecast.asJson
      case None => ForecastErrors.ForecastNotAvailable("Unable to create forecast at this moment. Please try again later.")
    }
  }
  val foreCastRoutes = HttpRoutes.of[F] {
    case GET -> Root / "forecast" / lat / long =>
      val validatedCoordinates =
        for {
          validLat <- validateCoordinate(lat)
          validLong <- validateCoordinate(long)
        } yield Coordinates(validLat, validLong)

      (for {
        coords <- Async[F].fromEither(validatedCoordinates)
        hourlyForecast <- weather.getHourlyForecast(coords)
        resp <- Ok(formatOutput(Output.getOutputForecastFromInput(hourlyForecast)))
      } yield resp).handleErrorWith {
        case e: ForecastErrors.InvalidCoordinates => BadRequest(e.asJson)
        case e: ForecastErrors.CoordinatesNotFound => NotFound(e.asJson)
        case e: ForecastErrors.ForecastNotAvailable => InternalServerError(e.asJson)
        case e => InternalServerError()
      }
  }
}