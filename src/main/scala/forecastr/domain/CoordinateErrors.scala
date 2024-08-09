package forecastr.domain

object CoordinateErrors {
  case class CoordinatesNotFound(message: String) extends Throwable
  case class CoordinatesTooLong(message: String) extends Throwable
}
