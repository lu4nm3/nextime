package nextime

import nextime.parsing.Parser
import validation.Rule
import nextime.implicits.EitherImplicits._

sealed abstract case class Hour(parts: List[HourPart]) extends MultipartExpression

object Hour {
  implicit val bounds: Bounds = Bounds(0, 23)

  def apply(hourExpression: String): Either[Error, Hour] = Parser.hour(hourExpression)

  def apply(head: HourPart, tail: HourPart*): Either[Error, Hour] = apply(head :: tail.toList)

  def apply(parts: List[HourPart]): Either[Error, Hour] = {
    if (parts.isEmpty) {
      Left(Error("Hour expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[HourPart]].errors) match {
        case errors if errors.nonEmpty => Left(Error("Invalid hour expression", errors))
        case _ => Right(new Hour(parts) {})
      }
    }
  }

  def unsafe(hourExpression: String): Hour = apply(hourExpression)

  def unsafe(head: HourPart, tail: HourPart*): Hour = apply(head :: tail.toList)

  def unsafe(parts: List[HourPart]): Hour = apply(parts)
}
