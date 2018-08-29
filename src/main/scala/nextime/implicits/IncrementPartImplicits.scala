package nextime
package implicits

trait IncrementPartImplicits {
  def /(value: Value): Increment = Increment(None, value)
}
