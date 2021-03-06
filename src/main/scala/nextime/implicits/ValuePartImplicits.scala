package nextime
package implicits

import scala.language.implicitConversions

trait ValuePartImplicits {
  implicit def intToValue(value: Int): Value = Value(value)

  implicit def valueToInt(value: Value): Int = value.value

  implicit class ValueOps[A](value: A)(implicit f: A => Value) {
    def ~-(upper: Value): Range = Range(value, upper)

    def ~/(increment: Value): Increment = Increment(Some(f(value)), increment)

    def L: LastDayOfMonth = LastDayOfMonth(f(value))

    def W: Weekday = Weekday(f(value))

    def ~#(occurrenceInMonth: Value): NthXDayOfMonth = NthXDayOfMonth(value, occurrenceInMonth)
  }

}
