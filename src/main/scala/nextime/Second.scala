package nextime

import validation.Rule

sealed abstract case class Second(parts: List[SecondPart]) extends MultipartExpression

object Second {
  implicit val bounds: Bounds = Bounds(0, 59)

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

  def unsafe(head: SecondPart, tail: SecondPart*): Second = unsafe(head :: tail.toList)

  def unsafe(parts: List[SecondPart]): Second = apply(parts) match {
    case Right(second) => second
    case Left(error) => throw error
  }
}
