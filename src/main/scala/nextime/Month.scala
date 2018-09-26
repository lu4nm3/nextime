package nextime

import validation.Rule

sealed abstract case class Month(parts: List[MonthPart]) extends MultipartExpression

object Month {
  implicit val bounds: Bounds = Bounds(1, 12)

  def apply(head: MonthPart, tail: MonthPart*): Either[Error, Month] = apply(head :: tail.toList)

  def apply(parts: List[MonthPart]): Either[Error, Month] = {
    if (parts.isEmpty) {
      Left(Error("Month expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[MonthPart]].errors) match {
        case errors if errors.nonEmpty => Left(Error("Invalid month expression", errors))
        case _ => Right(new Month(parts) {})
      }
    }
  }

  def unsafe(head: MonthPart, tail: MonthPart*): Month = unsafe(head :: tail.toList)

  def unsafe(parts: List[MonthPart]): Month = apply(parts) match {
    case Right(month) => month
    case Left(error) => throw error
  }
}
