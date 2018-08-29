package nextime

import validation.{Rule, Violation}

sealed abstract case class DayOfWeek(parts: List[DayOfWeekPart]) extends MultiPartExpression

object DayOfWeek {
  implicit val bounds: Bounds = Bounds(1, 7)

  def apply(head: DayOfWeekPart, tail: DayOfWeekPart*): Either[Violation, DayOfWeek] = apply(head :: tail.toList)

  def apply(parts: List[DayOfWeekPart]): Either[Violation, DayOfWeek] = {
    if (parts.isEmpty) {
      Left(Violation("Day of week expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[DayOfWeekPart]].violations) match {
        case violations if violations.nonEmpty => Left(Violation("Invalid day of week expression", violations.toList))
        case _ => Right(new DayOfWeek(parts) {})
      }
    }
  }

  def unsafe(head: DayOfWeekPart, tail: DayOfWeekPart*): DayOfWeek = unsafe(head :: tail.toList)

  def unsafe(parts: List[DayOfWeekPart]): DayOfWeek = apply(parts).right.get
}
