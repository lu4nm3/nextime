package nextime.time.intervals

import scala.collection.immutable.SortedSet

trait Interval[T] {
  def interval(part: T): SortedSet[Int]
}
