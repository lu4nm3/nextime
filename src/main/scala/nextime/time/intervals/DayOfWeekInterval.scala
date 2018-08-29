package nextime
package time
package intervals

import time.implicits._
import org.joda.time.DateTime

import scala.collection.immutable.SortedSet

trait DayOfWeekInterval extends BasePartIntervals {
  implicit def dayOfWeekInterval(implicit dateTime: DateTime): Interval[DayOfWeek] = new Interval[DayOfWeek] {

    import DayOfWeek._

    override def interval(dayOfWeek: DayOfWeek): SortedSet[Int] = {
      dayOfWeek.parts
        .map(baseIntervals orElse dayOfWeekIntervals)
        .foldLeft(SortedSet.empty[Int])(_ ++ _)
    }

    private val dayOfWeekIntervals: PartialFunction[PartExpression, SortedSet[Int]] = {
      case noValue@NoValue => noValue.interval
      case last@Last => last.interval
      case lastDayOfMonth@LastDayOfMonth(_) => lastDayOfMonth.interval
      case nthXDayOfMonth@NthXDayOfMonth(_, _) => nthXDayOfMonth.interval
    }

    private implicit def valueInterval(implicit dateTime: DateTime): Interval[Value] = new Interval[Value] {
      override def interval(value: Value): SortedSet[Int] = {
        SortedSet(getDaysOfMonth(value.value): _*)
      }
    }

    private implicit def rangeInterval(implicit bounds: Bounds): Interval[Range] = new Interval[Range] {
      override def interval(range: Range): SortedSet[Int] = {
        val interval = if (range.lower.value > range.upper.value) {
          SortedSet(range.lower.value to bounds.upper: _*) ++ SortedSet(bounds.lower to range.upper.value: _*)
        } else {
          SortedSet(range.lower.value to range.upper.value: _*)
        }

        interval.map(getDaysOfMonth).foldLeft(SortedSet.empty[Int])(_ ++ _)
      }
    }

    private implicit def allInterval(implicit bounds: Bounds): Interval[All.type] = new Interval[All.type] {
      def interval(all: All.type): SortedSet[Int] = {
        SortedSet(bounds.lower to bounds.upper flatMap getDaysOfMonth: _*)
      }
    }

    private implicit def incrementInterval(implicit bounds: Bounds): Interval[Increment] = new Interval[Increment] {
      override def interval(increment: Increment): SortedSet[Int] = increment match {
        case Increment(Some(bound), inc) => bound match {
          case Value(value) => SortedSet(value to bounds.upper by inc.value flatMap getDaysOfMonth: _*)
          case Range(lower, upper) =>
            if (lower.value > upper.value) {
              SortedSet(lower.value to bounds.upper by inc.value flatMap getDaysOfMonth: _*) ++
                SortedSet(bounds.lower to upper.value by inc.value flatMap getDaysOfMonth: _*)
            } else {
              SortedSet(lower.value to upper.value by inc.value flatMap getDaysOfMonth: _*)
            }
          case _: All.type => SortedSet(bounds.lower to bounds.upper by inc.value flatMap getDaysOfMonth: _*)
        }
        case Increment(None, inc) => SortedSet(bounds.lower to bounds.upper by inc.value flatMap getDaysOfMonth: _*)
      }
    }

    private implicit val noValueInterval: Interval[NoValue.type] = new Interval[NoValue.type] {
      override def interval(part: NoValue.type): SortedSet[Int] = {
        SortedSet.empty[Int]
      }
    }

    private implicit def lastInterval(implicit bounds: Bounds, dateTime: DateTime): Interval[Last.type] = {
      new Interval[Last.type] {
        override def interval(part: Last.type): SortedSet[Int] = {
          SortedSet(getDaysOfMonth(bounds.upper): _*)
        }
      }
    }

    private implicit def lastDayOfMonthInterval(implicit bounds: Bounds,
                                                dateTime: DateTime): Interval[LastDayOfMonth] = {
      new Interval[LastDayOfMonth] {
        override def interval(part: LastDayOfMonth): SortedSet[Int] = {
          SortedSet(lastDayOfMonth(part.value.value))
        }

        private def lastDayOfMonth(dayOfWeek: Int): Int = {
          val lastDayOfMonth = dateTime.lastDayOfMonth
          val lastDayOfWeekForMonth = dateTime.withDayOfMonth(lastDayOfMonth).normalizedDayOfWeek
          val dateTimeWithLastDayOfMonth = dateTime.withDayOfMonth(lastDayOfMonth)

          if (dayOfWeek <= lastDayOfWeekForMonth) {
            dateTimeWithLastDayOfMonth.minusDays(lastDayOfWeekForMonth - dayOfWeek).getDayOfMonth
          } else {
            dateTimeWithLastDayOfMonth.minusDays(bounds.upper - dayOfWeek + lastDayOfWeekForMonth).getDayOfMonth
          }
        }
      }
    }

    private implicit def nthXDayOfMonthInterval(implicit dateTime: DateTime): Interval[NthXDayOfMonth] = {
      new Interval[NthXDayOfMonth] {
        override def interval(part: NthXDayOfMonth): SortedSet[Int] = {
          nthXDayOfMonth(part).map(SortedSet[Int](_)).getOrElse(SortedSet.empty[Int])
        }

        private def nthXDayOfMonth(part: NthXDayOfMonth): Option[Int] = {
          (1 to dateTime.lastDayOfMonth)
            .filter(day => dateTime.withDayOfMonth(day).normalizedDayOfWeek == part.dayOfWeek.value)
            .lift(part.occurrenceInMonth.value - 1)
        }
      }
    }

    private def getDaysOfMonth(dayOfWeek: Int)(implicit dateTime: DateTime): Seq[Int] = {
      val lastDayOfMonth = dateTime.lastDayOfMonth
      (1 to lastDayOfMonth).filter(day => dateTime.withDayOfMonth(day).normalizedDayOfWeek == dayOfWeek)
    }
  }
}
