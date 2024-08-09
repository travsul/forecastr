package forecastr.logic

import forecastr.domain.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AsyncFreeSpec

import java.time.*

class OutputSpec extends AsyncFreeSpec with Matchers {
  def getForecast(temperature: Int, shortForecast: String): Forecast = {
    val startTime = OffsetDateTime.of(2024, 8, 8, 0, 0, 0, 0, ZoneOffset.of("Z"))
    val endTime = OffsetDateTime.of(2024, 8, 8, 23, 59, 59, 0, ZoneOffset.of("Z"))
    val period = Periods(
      startTime = startTime,
      endTime = endTime,
      temperature = temperature,
      temperatureUnit = "F",
      shortForecast = shortForecast
    )
    Forecast(List(period))
  }
  "Output" - {
    "Should find the correct classification when hot" in {
      Output.getOutputForecastFromInput(getForecast(81, "It's still sunny")) shouldBe Some(OutputForecast("hot", "It's still sunny"))
    }
    "Should find the correct classification when cold" in {
      Output.getOutputForecastFromInput(getForecast(36, "It's getting chilly")) shouldBe Some(OutputForecast("cold", "It's getting chilly"))
    }
    "Should find the correct classification when moderate" in {
      Output.getOutputForecastFromInput(getForecast(53, "Light Jacket")) shouldBe Some(OutputForecast("moderate", "Light Jacket"))
    }
  }
}