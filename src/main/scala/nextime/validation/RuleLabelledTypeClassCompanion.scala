package nextime.validation

import nextime.Violation
import shapeless.{:+:, ::, CNil, Coproduct, HList, HNil, Inl, Inr, LabelledTypeClass, LabelledTypeClassCompanion}

trait RuleLabelledTypeClassCompanion extends LabelledTypeClassCompanion[Rule] {

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
