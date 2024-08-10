package forecastr.domain

object ForecastErrors {
  case class CoordinatesNotFound(message: String) extends Throwable
  case class InvalidCoordinates(message: String) extends Throwable
  case class ForecastNotAvailable(message: String) extends Throwable
}
