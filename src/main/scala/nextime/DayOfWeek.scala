package nextime

import nextime.parsing.Parser
import validation.Rule
import nextime.implicits.EitherImplicits._

sealed abstract case class DayOfWeek(parts: List[DayOfWeekPart]) extends MultipartExpression

object DayOfWeek {
  implicit val bounds: Bounds = Bounds(1, 7)

  def apply(dayOfWeekExpression: String): Either[Error, DayOfWeek] = Parser.dayOfWeek(dayOfWeekExpression)

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

  def unsafe(dayOfWeekExpression: String): DayOfWeek = apply(dayOfWeekExpression)

  def unsafe(head: DayOfWeekPart, tail: DayOfWeekPart*): DayOfWeek = apply(head :: tail.toList)

  def unsafe(parts: List[DayOfWeekPart]): DayOfWeek = apply(parts)
}
