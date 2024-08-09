package forecastr.domain

import io.circe.*
import io.circe.generic.semiauto.*

case class OutputForecast(
  classification: String,
  shortForecast: String
)

object OutputForecast {
  given outputForecastEncoder: Encoder[OutputForecast] = deriveEncoder[OutputForecast]
}