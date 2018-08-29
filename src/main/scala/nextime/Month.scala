package nextime

import validation.{Rule, Violation}

sealed abstract case class Month(parts: List[MonthPart]) extends MultiPartExpression

object Month {
  implicit val bounds: Bounds = Bounds(1, 12)

  def apply(head: MonthPart, tail: MonthPart*): Either[Violation, Month] = apply(head :: tail.toList)

  def apply(parts: List[MonthPart]): Either[Violation, Month] = {
    if (parts.isEmpty) {
      Left(Violation("Month expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[MonthPart]].violations) match {
        case violations if violations.nonEmpty => Left(Violation("Invalid month expression", violations.toList))
        case _ => Right(new Month(parts) {})
      }
    }
  }

  def unsafe(head: MonthPart, tail: MonthPart*): Month = unsafe(head :: tail.toList)

  def unsafe(parts: List[MonthPart]): Month = apply(parts).right.get
}
