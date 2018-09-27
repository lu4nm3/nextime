package nextime

import nextime.parsing.Parser
import validation.Rule
import nextime.implicits.EitherImplicits._

sealed abstract case class Year(parts: List[YearPart]) extends MultipartExpression

object Year {
  implicit val bounds: Bounds = Bounds(1979, 2099)

  def apply(yearExpression: String): Either[Error, Year] = Parser.year(yearExpression)

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

  def unsafe(yearExpression: String): Year = apply(yearExpression)

  def unsafe(head: YearPart, tail: YearPart*): Year = apply(head :: tail.toList)

  def unsafe(parts: List[YearPart]): Year = apply(parts)
}
