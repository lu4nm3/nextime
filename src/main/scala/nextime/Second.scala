package nextime

import nextime.parsing.Parser
import nextime.validation.Rule
import nextime.implicits.EitherImplicits._

sealed abstract case class Second(parts: List[SecondPart]) extends MultipartExpression

object Second {
  implicit val bounds: Bounds = Bounds(0, 59)

  def apply(secondExpression: String): Either[Error, Second] = Parser.second(secondExpression)

  def apply(head: SecondPart, tail: SecondPart*): Either[Error, Second] = apply(head :: tail.toList)

  def apply(parts: List[SecondPart]): Either[Error, Second] = {
    if (parts.isEmpty) {
      Left(Error("Second expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[SecondPart]].errors) match {
        case errors if errors.nonEmpty => Left(Error("Invalid second expression", errors))
        case _ => Right(new Second(parts) {})
      }
    }
  }

  def unsafe(secondExpression: String): Second = apply(secondExpression)

  def unsafe(head: SecondPart, tail: SecondPart*): Second = apply(head :: tail.toList)

  def unsafe(parts: List[SecondPart]): Second = apply(parts)
}
