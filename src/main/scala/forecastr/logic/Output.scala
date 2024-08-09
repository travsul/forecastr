package forecastr.logic

import forecastr.domain.*

object Output {
  def getOutputForecastFromInput(forecast: Forecast): Option[OutputForecast] = {
    val currentForecast = forecast.periods.sortBy(_.startTime).headOption
    val tempClassification: Option[String] = currentForecast.map { f =>
      if (f.temperature >= 80) "hot"
      else if (f.temperature >= 50) "moderate"
      else "cold"
    }
    for {
      classification <- tempClassification
      shortForecast <- currentForecast.map(_.shortForecast)
    } yield {
      OutputForecast(
        classification = classification,
        shortForecast = shortForecast
      )
    }
  }
}