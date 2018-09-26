package nextime.validation

import nextime.Error

trait Rule[A] {
  def errors(a: A): Vector[Error]
}

object Rule extends RuleLabelledTypeClassCompanion with Rules {

  case class Pure[E, A](f: A => Vector[Error]) extends Rule[A] {
    def errors(a: A): Vector[Error] = f(a)
  }

  def apply[E, A](f: A => Vector[Error]): Rule[A] = Pure(f)

  def empty[A]: Rule[A] = apply(_ => Vector.empty[Error])

  def from[E, A](errors: Vector[Error], f: A => Boolean): Rule[A] = Pure(a =>
    if (f(a)) Vector.empty[Error]
    else errors
  )

  def from[E, A](errors: A => Vector[Error], f: A => Boolean): Rule[A] = Pure(a =>
    if (f(a)) Vector.empty[Error]
    else errors(a)
  )
}
