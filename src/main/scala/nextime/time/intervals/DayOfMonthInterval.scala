package nextime
package time
package intervals

import time.implicits._
import org.joda.time.DateTime

import scala.collection.immutable.SortedSet

trait DayOfMonthInterval extends BasePartIntervals {
  implicit def dayOfMonthInterval(implicit dateTime: DateTime): Interval[DayOfMonth] = new Interval[DayOfMonth] {

    import DayOfMonth._

    override def interval(dayOfMonth: DayOfMonth): SortedSet[Int] = {
      dayOfMonth.parts
        .map(baseIntervals orElse dayOfMonthIntervals)
        .foldLeft(SortedSet.empty[Int])(_ ++ _)
    }

    private val dayOfMonthIntervals: PartialFunction[PartExpression, SortedSet[Int]] = {
      case noValue@NoValue => noValue.interval
      case last@Last => last.interval
      case lastOffset@LastOffset(_) => lastOffset.interval
      case weekday@Weekday(_) => weekday.interval
      case lastWeekday@LastWeekday => lastWeekday.interval
    }

    private implicit val noValueInterval: Interval[NoValue.type] = new Interval[NoValue.type] {
      override def interval(part: NoValue.type): SortedSet[Int] = {
        SortedSet.empty[Int]
      }
    }

    private implicit def lastInterval(implicit dateTime: DateTime): Interval[Last.type] = new Interval[Last.type] {
      override def interval(part: Last.type): SortedSet[Int] = {
        SortedSet(dateTime.lastDayOfMonth)
      }
    }

    private implicit def lastOffsetInterval(implicit dateTime: DateTime): Interval[LastOffset] = {
      new Interval[LastOffset] {
        override def interval(last: LastOffset): SortedSet[Int] = {
          lastDayFromOffset(last.value.value) match {
            case Some(day) => SortedSet(day)
            case None => SortedSet.empty[Int]
          }
        }

        private def lastDayFromOffset(offset: Int): Option[Int] = {
          val lastDayOfMonth = dateTime.lastDayOfMonth

          if (lastDayOfMonth > offset) {
            Some(lastDayOfMonth - offset)
          } else {
            None
          }
        }
      }
    }

    private implicit def weekdayInterval(implicit dateTime: DateTime): Interval[Weekday] = new Interval[Weekday] {
      override def interval(weekday: Weekday): SortedSet[Int] = {
        nearestWeekday(weekday.value.value) match {
          case Some(day) => SortedSet(day)
          case None => SortedSet.empty[Int]
        }
      }

      private def nearestWeekday(dayOfMonth: Int): Option[Int] = {
        val lastDayOfMonth = dateTime.lastDayOfMonth

        if (dayOfMonth < 1 || dayOfMonth > lastDayOfMonth) {
          None
        } else {
          val currentMonth = dateTime.getMonthOfYear
          val dayOfWeek = dateTime.withDayOfMonth(dayOfMonth).normalizedDayOfWeek

          if (dayOfWeek >= 2 && dayOfWeek <= 6) {
            // Weekday
            Some(dayOfMonth)
          } else if (dayOfWeek == 1) {
            // Sunday
            val nextDay = dateTime.withDayOfMonth(dayOfMonth).plusDays(1)

            if (nextDay.getMonthOfYear != currentMonth) {
              Some(dateTime.withDayOfMonth(dayOfMonth).minusDays(2).getDayOfMonth) // Friday
            } else {
              Some(nextDay.getDayOfMonth) // Monday
            }
          } else {
            // Saturday
            val previousDay = dateTime.withDayOfMonth(dayOfMonth).minusDays(1)

            if (previousDay.getMonthOfYear != currentMonth) {
              Some(dateTime.withDayOfMonth(dayOfMonth).plusDays(2).getDayOfMonth) // Monday
            } else {
              Some(previousDay.getDayOfMonth) // Friday
            }
          }
        }
      }
    }

    private implicit def lastWeekdayInterval(implicit dateTime: DateTime): Interval[LastWeekday.type] = {
      new Interval[LastWeekday.type] {
        override def interval(last: LastWeekday.type): SortedSet[Int] = {
          SortedSet(lastWeekday)
        }

        private def lastWeekday: Int = {
          val lastDayOfMonth = dateTime.withDayOfMonth(dateTime.lastDayOfMonth)
          val dayOfWeek = lastDayOfMonth.normalizedDayOfWeek

          if (dayOfWeek >= 2 && dayOfWeek <= 6) {
            lastDayOfMonth.getDayOfMonth
          } else {
            // if last day of month falls on the weekend, last weekday will always be a Friday
            lastDayOfMonth.withDayOfWeek(5).getDayOfMonth
          }
        }
      }
    }
  }
}
