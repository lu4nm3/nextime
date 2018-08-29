package nextime

import validation.{Rule, Violation}

sealed abstract case class Second(parts: List[SecondPart]) extends MultiPartExpression

object Second {
  implicit val bounds: Bounds = Bounds(0, 59)

  def apply(head: SecondPart, tail: SecondPart*): Either[Violation, Second] = apply(head :: tail.toList)

  def apply(parts: List[SecondPart]): Either[Violation, Second] = {
    if (parts.isEmpty) {
      Left(Violation("Second expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[SecondPart]].violations) match {
        case violations if violations.nonEmpty => Left(Violation("Invalid second expression", violations.toList))
        case _ => Right(new Second(parts) {})
      }
    }
  }

  def unsafe(head: SecondPart, tail: SecondPart*): Second = unsafe(head :: tail.toList)

  def unsafe(parts: List[SecondPart]): Second = apply(parts).right.get
}
