package forecastr.domain

import io.circe._
import io.circe.generic.semiauto._

case class Points(
  gridId: String,
  gridX: Int,
  gridY: Int
)

object Points {
  given pointsDecoder: Decoder[Points] = new Decoder[Points] {
    final def apply(c: HCursor): Decoder.Result[Points] = {
      for {
        gridId <- c.downField("properties").downField("gridId").as[String]
        gridX <- c.downField("properties").downField("gridX").as[Int]
        gridY <- c.downField("properties").downField("gridY").as[Int]
      } yield Points(gridId, gridX, gridY)
    }
  }
  given pointsEncoder: Encoder[Points] = deriveEncoder[Points]
}