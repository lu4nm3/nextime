package nextime

import validation.{Rule, Violation}

sealed abstract case class Minute(parts: List[MinutePart]) extends MultipartExpression

object Minute {
  implicit val bounds: Bounds = Bounds(0, 59)

  def apply(head: MinutePart, tail: MinutePart*): Either[Violation, Minute] = apply(head :: tail.toList)

  def apply(parts: List[MinutePart]): Either[Violation, Minute] = {
    if (parts.isEmpty) {
      Left(Violation("Minute expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[MinutePart]].violations) match {
        case violations if violations.nonEmpty => Left(Violation("Invalid minute expression", violations.toList))
        case _ => Right(new Minute(parts) {})
      }
    }
  }

  def unsafe(head: MinutePart, tail: MinutePart*): Minute = unsafe(head :: tail.toList)

  def unsafe(parts: List[MinutePart]): Minute = apply(parts).right.get
}
