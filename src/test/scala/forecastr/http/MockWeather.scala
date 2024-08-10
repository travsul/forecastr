package forecastr.http

import cats.effect.IO
import forecastr.domain.{Coordinates, Forecast, Periods}

import java.time.{OffsetDateTime, ZoneOffset}

class MockWeather(boom: Boolean = false) extends Weather[IO] {
  override def getHourlyForecast(latLong: Coordinates): IO[Forecast] = {
    if (!boom) {
      val startTime = OffsetDateTime.of(2024, 8, 8, 0, 0, 0, 0, ZoneOffset.of("Z"))
      val endTime = OffsetDateTime.of(2024, 8, 8, 23, 59, 59, 0, ZoneOffset.of("Z"))
      val period = Periods(
        startTime = startTime,
        endTime = endTime,
        temperature = 36,
        temperatureUnit = "F",
        shortForecast = "It's still sunny"
      )
      IO.pure(Forecast(List(period)))
    }
    else {
      IO.raiseError(new Throwable("BOOM!"))
    }
  }
}