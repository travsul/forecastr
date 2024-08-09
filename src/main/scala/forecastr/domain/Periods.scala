package forecastr.domain

import io.circe.*
import io.circe.generic.semiauto.*

import java.time.OffsetDateTime

case class Periods(
  startTime: OffsetDateTime,
  endTime: OffsetDateTime,
  temperature: Int,
  temperatureUnit: String,
  shortForecast: String
)

object Periods {
  given periodsDecoder: Decoder[Periods] = (c: HCursor) => {
    for {
      startTime <- c.downField("startTime").as[OffsetDateTime]
      endTime <- c.downField("endTime").as[OffsetDateTime]
      temperature <- c.downField("temperature").as[Int]
      temperatureUnit <- c.downField("temperatureUnit").as[String]
      shortForecast <- c.downField("shortForecast").as[String]
    } yield Periods(startTime, endTime, temperature, temperatureUnit, shortForecast)
  }
  
  given periodsEncoder: Encoder[Periods] = deriveEncoder[Periods]
}