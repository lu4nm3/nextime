package nextime
package time
package intervals

import org.joda.time.DateTime

import scala.collection.immutable.SortedSet

trait MultiPartExpressionIntervals extends BasePartIntervals {
  implicit def secondsInterval(implicit dateTime: DateTime): Interval[Second] = new Interval[Second] {

    import Second._

    override def interval(second: Second): SortedSet[Int] = {
      second.parts
        .map(baseIntervals)
        .foldLeft(SortedSet.empty[Int])(_ ++ _)
    }
  }

  implicit def minutesInterval(implicit dateTime: DateTime): Interval[Minute] = new Interval[Minute] {

    import Minute._

    override def interval(minute: Minute): SortedSet[Int] = {
      minute.parts
        .map(baseIntervals)
        .foldLeft(SortedSet.empty[Int])(_ ++ _)
    }
  }

  implicit def hoursInterval(implicit dateTime: DateTime): Interval[Hour] = new Interval[Hour] {

    import Hour._

    override def interval(hour: Hour): SortedSet[Int] = {
      hour.parts
        .map(baseIntervals)
        .foldLeft(SortedSet.empty[Int])(_ ++ _)
    }
  }

  implicit def monthInterval(implicit dateTime: DateTime): Interval[Month] = new Interval[Month] {

    import Month._

    override def interval(month: Month): SortedSet[Int] = {
      month.parts
        .map(baseIntervals)
        .foldLeft(SortedSet.empty[Int])(_ ++ _)
    }
  }

  implicit def yearInterval(implicit dateTime: DateTime): Interval[Year] = new Interval[Year] {

    import Year._

    override def interval(year: Year): SortedSet[Int] = {
      year.parts
        .map(baseIntervals)
        .foldLeft(SortedSet.empty[Int])(_ ++ _)
    }
  }
}
