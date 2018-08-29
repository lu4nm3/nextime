package nextime
package implicits

trait RangePartImplicits {

  implicit class RangeOps[A](range: A)(implicit f: A => Range) {
    def -/-(increment: Value): Increment = Increment(Some(f(range)), increment)
  }

}
