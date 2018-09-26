package nextime

import validation.Rule

sealed abstract case class DayOfMonth(parts: List[DayOfMonthPart]) extends MultipartExpression

object DayOfMonth {
  implicit val bounds: Bounds = Bounds(1, 31)

  def apply(head: DayOfMonthPart, tail: DayOfMonthPart*): Either[Error, DayOfMonth] = apply(head :: tail.toList)

  def apply(parts: List[DayOfMonthPart]): Either[Error, DayOfMonth] = {
    if (parts.isEmpty) {
      Left(Error("Day of month expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[DayOfMonthPart]].errors) match {
        case errors if errors.nonEmpty => Left(Error("Invalid day of month expression", errors))
        case _ => Right(new DayOfMonth(parts) {})
      }
    }
  }

  def unsafe(head: DayOfMonthPart, tail: DayOfMonthPart*): DayOfMonth = unsafe(head :: tail.toList)

  def unsafe(parts: List[DayOfMonthPart]): DayOfMonth = apply(parts) match {
    case Right(dayOfMonth) => dayOfMonth
    case Left(error) => throw error
  }
}
