package nextime.time

import scala.collection.immutable.SortedSet

package object intervals extends AllIntervals {

  implicit class IntervalOps[T](value: T) {
    def interval(implicit I: Interval[T]): SortedSet[Int] = {
      I.interval(value)
    }
  }

}
