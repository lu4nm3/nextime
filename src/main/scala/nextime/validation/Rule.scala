package nextime.validation

import shapeless.{:+:, ::, CNil, Coproduct, HList, HNil, Inl, Inr, LabelledTypeClass, LabelledTypeClassCompanion}

trait Rule[A] {
  def violations(a: A): Vector[Violation]
}

object Rule extends LabelledTypeClassCompanion[Rule] with Rules {

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

  object typeClass extends LabelledTypeClass[Rule] {
    def emptyProduct: Rule[HNil] = new Rule[HNil] {
      def violations(t: HNil) = Vector.empty[Violation]
    }

    def product[F, T <: HList](name: String, sh: Rule[F], st: Rule[T]): Rule[F :: T] = new Rule[F :: T] {
      def violations(ft: F :: T): Vector[Violation] = Vector.empty[Violation]
    }

    def emptyCoproduct: Rule[CNil] = new Rule[CNil] {
      def violations(t: CNil) = Vector.empty[Violation]
    }

    def coproduct[L, R <: Coproduct](name: String, rl: => Rule[L], rr: => Rule[R]): Rule[L :+: R] = new Rule[L :+: R] {
      def violations(lr: L :+: R): Vector[Violation] = lr match {
        case Inl(l) => rl.violations(l)
        case Inr(r) => rr.violations(r)
      }
    }

    def project[F, G](instance: => Rule[G], to: F => G, from: G => F): Rule[F] = new Rule[F] {
      def violations(f: F): Vector[Violation] = instance.violations(to(f))
    }
  }

}
