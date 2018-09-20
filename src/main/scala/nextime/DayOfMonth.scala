package nextime

import validation.{Rule, Violation}

sealed abstract case class DayOfMonth(parts: List[DayOfMonthPart]) extends MultipartExpression

object DayOfMonth {
  implicit val bounds: Bounds = Bounds(1, 31)

  def apply(head: DayOfMonthPart, tail: DayOfMonthPart*): Either[Violation, DayOfMonth] = {
    apply(head :: tail.toList)
  }

  def apply(parts: List[DayOfMonthPart]): Either[Violation, DayOfMonth] = {
    if (parts.isEmpty) {
      Left(Violation("Day of month expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[DayOfMonthPart]].violations) match {
        case violations if violations.nonEmpty => Left(Violation("Invalid day of month expression", violations.toList))
        case _ => Right(new DayOfMonth(parts) {})
      }
    }
  }

  def unsafe(head: DayOfMonthPart, tail: DayOfMonthPart*): DayOfMonth = unsafe(head :: tail.toList)

  def unsafe(parts: List[DayOfMonthPart]): DayOfMonth = apply(parts).right.get
}
