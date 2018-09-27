package nextime

import nextime.parsing.Parser
import validation.Rule
import nextime.implicits.EitherImplicits._

sealed abstract case class DayOfMonth(parts: List[DayOfMonthPart]) extends MultipartExpression

object DayOfMonth {
  implicit val bounds: Bounds = Bounds(1, 31)

  def apply(dayOfMonthExpression: String): Either[Error, DayOfMonth] = Parser.dayOfMonth(dayOfMonthExpression)

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

  def unsafe(dayOfMonthExpression: String): DayOfMonth = apply(dayOfMonthExpression)

  def unsafe(head: DayOfMonthPart, tail: DayOfMonthPart*): DayOfMonth = apply(head :: tail.toList)

  def unsafe(parts: List[DayOfMonthPart]): DayOfMonth = apply(parts)
}
