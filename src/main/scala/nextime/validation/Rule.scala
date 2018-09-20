package nextime.validation

trait Rule[A] {
  def violations(a: A): Vector[Violation]
}

object Rule extends RuleLabelledTypeClassCompanion with Rules {

  case class Pure[E, A](f: A => Vector[Violation]) extends Rule[A] {
    def violations(a: A): Vector[Violation] = f(a)
  }

  def apply[E, A](f: A => Vector[Violation]): Rule[A] = Pure(f)

  def empty[A]: Rule[A] = apply(_ => Vector.empty[Violation])

  def from[E, A](violations: Vector[Violation], f: A => Boolean): Rule[A] = Pure(a =>
    if (f(a)) Vector.empty[Violation]
    else violations
  )

  def from[E, A](violations: A => Vector[Violation], f: A => Boolean): Rule[A] = Pure(a =>
    if (f(a)) Vector.empty[Violation]
    else violations(a)
  )
}
