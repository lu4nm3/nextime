package nextime
package implicits

trait AllPartImplicits {

  implicit class AllOps[A](all: A)(implicit f: A => All.type) {
    def -/-(increment: Value): Increment = Increment(Some(f(all)), increment)
  }

}
