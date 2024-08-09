package forecastr.domain

import io.circe.*
import io.circe.generic.semiauto.*

case class Forecast(
  periods: List[Periods]
)

object Forecast {
  given forecastDecoder: Decoder[Forecast] = (c: HCursor) => {
    for {
      periods <- c.downField("properties").downField("periods").as[List[Periods]]
    } yield Forecast(periods)
  }

  given forecastEncoder: Encoder[Forecast] = deriveEncoder[Forecast]
}