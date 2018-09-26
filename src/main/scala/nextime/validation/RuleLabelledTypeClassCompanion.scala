package nextime.validation

import nextime.Error
import shapeless.{:+:, ::, CNil, Coproduct, HList, HNil, Inl, Inr, LabelledTypeClass, LabelledTypeClassCompanion}

trait RuleLabelledTypeClassCompanion extends LabelledTypeClassCompanion[Rule] {

  object typeClass extends LabelledTypeClass[Rule] {
    def emptyProduct: Rule[HNil] = new Rule[HNil] {
      def errors(t: HNil) = Vector.empty[Error]
    }

    def product[F, T <: HList](name: String, sh: Rule[F], st: Rule[T]): Rule[F :: T] = new Rule[F :: T] {
      def errors(ft: F :: T): Vector[Error] = Vector.empty[Error]
    }

    def emptyCoproduct: Rule[CNil] = new Rule[CNil] {
      def errors(t: CNil) = Vector.empty[Error]
    }

    def coproduct[L, R <: Coproduct](name: String, rl: => Rule[L], rr: => Rule[R]): Rule[L :+: R] = new Rule[L :+: R] {
      def errors(lr: L :+: R): Vector[Error] = lr match {
        case Inl(l) => rl.errors(l)
        case Inr(r) => rr.errors(r)
      }
    }

    def project[F, G](instance: => Rule[G], to: F => G, from: G => F): Rule[F] = new Rule[F] {
      def errors(f: F): Vector[Error] = instance.errors(to(f))
    }
  }

}
