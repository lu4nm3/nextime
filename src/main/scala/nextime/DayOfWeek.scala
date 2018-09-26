package nextime

import validation.Rule

sealed abstract case class DayOfWeek(parts: List[DayOfWeekPart]) extends MultipartExpression

object DayOfWeek {
  implicit val bounds: Bounds = Bounds(1, 7)

  def apply(head: DayOfWeekPart, tail: DayOfWeekPart*): Either[Error, DayOfWeek] = apply(head :: tail.toList)

  def apply(parts: List[DayOfWeekPart]): Either[Error, DayOfWeek] = {
    if (parts.isEmpty) {
      Left(Error("Day of week expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[DayOfWeekPart]].errors) match {
        case errors if errors.nonEmpty => Left(Error("Invalid day of week expression", errors))
        case _ => Right(new DayOfWeek(parts) {})
      }
    }
  }

  def unsafe(head: DayOfWeekPart, tail: DayOfWeekPart*): DayOfWeek = unsafe(head :: tail.toList)

  def unsafe(parts: List[DayOfWeekPart]): DayOfWeek = apply(parts) match {
    case Right(dayOfWeek) => dayOfWeek
    case Left(error) => throw error
  }
}
