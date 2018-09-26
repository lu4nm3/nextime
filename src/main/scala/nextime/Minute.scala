package nextime

import validation.Rule

sealed abstract case class Minute(parts: List[MinutePart]) extends MultipartExpression

object Minute {
  implicit val bounds: Bounds = Bounds(0, 59)

  def apply(head: MinutePart, tail: MinutePart*): Either[Error, Minute] = apply(head :: tail.toList)

  def apply(parts: List[MinutePart]): Either[Error, Minute] = {
    if (parts.isEmpty) {
      Left(Error("Minute expression must not be empty"))
    } else {
      parts.flatMap(implicitly[Rule[MinutePart]].errors) match {
        case errors if errors.nonEmpty => Left(Error("Invalid minute expression", errors))
        case _ => Right(new Minute(parts) {})
      }
    }
  }

  def unsafe(head: MinutePart, tail: MinutePart*): Minute = unsafe(head :: tail.toList)

  def unsafe(parts: List[MinutePart]): Minute = apply(parts) match {
    case Right(minute) => minute
    case Left(error) => throw error
  }
}
