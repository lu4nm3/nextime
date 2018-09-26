package nextime

import validation.Rule

sealed abstract case class Year(parts: List[YearPart]) extends MultipartExpression

object Year {
  implicit val bounds: Bounds = Bounds(1979, 2099)

  def apply(head: YearPart, tail: YearPart*): Either[Error, Year] = apply(head :: tail.toList)

  def apply(parts: List[YearPart]): Either[Error, Year] = {
    if (parts.isEmpty) {
      Left(Error("Year expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[YearPart]].errors) match {
        case errors if errors.nonEmpty => Left(Error("Invalid year expression", errors))
        case _ => Right(new Year(parts) {})
      }
    }
  }

  def unsafe(head: YearPart, tail: YearPart*): Year = unsafe(head :: tail.toList)

  def unsafe(parts: List[YearPart]): Year = apply(parts) match {
    case Right(year) => year
    case Left(error) => throw error
  }
}
