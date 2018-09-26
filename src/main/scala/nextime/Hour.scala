package nextime

import validation.Rule

sealed abstract case class Hour(parts: List[HourPart]) extends MultipartExpression

object Hour {
  implicit val bounds: Bounds = Bounds(0, 23)

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

  def unsafe(head: HourPart, tail: HourPart*): Hour = unsafe(head :: tail.toList)

  def unsafe(parts: List[HourPart]): Hour = apply(parts) match {
    case Right(hour) => hour
    case Left(error) => throw error
  }
}
