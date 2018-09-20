package nextime

import validation.{Rule, Violation}

sealed abstract case class Year(parts: List[YearPart]) extends MultipartExpression

object Year {
  implicit val bounds: Bounds = Bounds(1979, 2099)

  def apply(head: YearPart, tail: YearPart*): Either[Violation, Year] = apply(head :: tail.toList)

  def apply(parts: List[YearPart]): Either[Violation, Year] = {
    if (parts.isEmpty) {
      Left(Violation("Year expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[YearPart]].violations) match {
        case violations if violations.nonEmpty => Left(Violation("Invalid year expression", violations.toList))
        case _ => Right(new Year(parts) {})
      }
    }
  }

  def unsafe(head: YearPart, tail: YearPart*): Year = unsafe(head :: tail.toList)

  def unsafe(parts: List[YearPart]): Year = apply(parts).right.get
}
