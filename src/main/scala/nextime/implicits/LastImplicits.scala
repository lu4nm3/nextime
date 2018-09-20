package nextime
package implicits

trait LastImplicits {
  implicit class LastOps[A](last: A)(implicit f: A => Last.type) {
    def -(value: Value): LastOffset = LastOffset(value)
  }
}
