package nextime.implicits

import nextime.Error

import scala.language.implicitConversions

trait EitherImplicits {
  implicit def toUnsafe[A](value: Either[Error, A]): A = value match {
    case Right(v) => v
    case Left(error) => throw error
  }
}

object EitherImplicits extends EitherImplicits
