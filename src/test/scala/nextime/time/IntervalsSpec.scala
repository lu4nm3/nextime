package nextime
package time

import nextime.time.intervals._
import org.joda.time.DateTime
import org.scalatest.Matchers._
import org.scalatest.WordSpec

import scala.collection.immutable.SortedSet

class IntervalsSpec extends WordSpec {
  implicit val dateTime: DateTime = date(2018, 1, 1, 0, 0, 0)

  implicit def subExpressionInterval(implicit dateTime: DateTime): Interval[MultipartExpression] = new Interval[MultipartExpression] {
    override def interval(subExpression: MultipartExpression): SortedSet[Int] = subExpression match {
      case seconds@Second(_) => secondsInterval.interval(seconds)
      case minutes@Minute(_) => minutesInterval.interval(minutes)
      case hours@Hour(_) => hoursInterval.interval(hours)
      case dayOfMonth@DayOfMonth(_) => dayOfMonthInterval.interval(dayOfMonth)
      case month@Month(_) => monthInterval.interval(month)
      case dayOfWeek@DayOfWeek(_) => dayOfWeekInterval.interval(dayOfWeek)
      case year@Year(_) => yearInterval.interval(year)
    }
  }

  def assertInterval(subExpression: Either[Violation, MultipartExpression], expectedIntervals: SortedSet[Int])(implicit dateTime: DateTime) = {
    assertResult(expectedIntervals)(subExpression.right.get.interval)
  }

  "Intervals generated" when {

    "evaluating parts" should {
      "expand value" in {
        val value = Value(3)

        //        val interval = value.interval
        //
        //        interval should contain(3)
      }
    }

    "evaluating subexpressions" should {
      "expand seconds" in {
        assertInterval(Second(Value(1)), SortedSet(1))
        assertInterval(Second(Range(Value(1), Value(3))), SortedSet(1, 2, 3))
        assertInterval(Second(Range(Value(3), Value(1))), SortedSet((3 to 59) ++ (0 to 1): _*))
        assertInterval(Second(All), SortedSet(0 to 59: _*))
        assertInterval(Second(Increment(Value(1), Value(3))), SortedSet(1 to 59 by 3: _*))
        assertInterval(Second(Increment(Range(Value(1), Value(3)), Value(3))), SortedSet(1 to 3 by 3: _*))
        assertInterval(
          Second(Increment(Range(Value(3), Value(1)), Value(3))),
          SortedSet((3 to 59 by 3) ++ (0 to 1 by 3): _*)
        )
        assertInterval(Second(Increment(All, Value(3))), SortedSet(0 to 59 by 3: _*))
        assertInterval(Second(Increment(Value(3))), SortedSet(0 to 59 by 3: _*))
      }

      "expand minutes" in {
        assertInterval(Minute(Value(1)), SortedSet(1))
        assertInterval(Minute(Range(Value(1), Value(3))), SortedSet(1, 2, 3))
        assertInterval(Minute(Range(Value(3), Value(1))), SortedSet((3 to 59) ++ (0 to 1): _*))
        assertInterval(Minute(All), SortedSet(0 to 59: _*))
        assertInterval(Minute(Increment(Value(1), Value(3))), SortedSet(1 to 59 by 3: _*))
        assertInterval(Minute(Increment(Range(Value(1), Value(3)), Value(3))), SortedSet(1 to 3 by 3: _*))
        assertInterval(
          Minute(Increment(Range(Value(3), Value(1)), Value(3))),
          SortedSet((3 to 59 by 3) ++ (0 to 1 by 3): _*)
        )
        assertInterval(Minute(Increment(All, Value(3))), SortedSet(0 to 59 by 3: _*))
        assertInterval(Minute(Increment(Value(3))), SortedSet(0 to 59 by 3: _*))
      }

      "expand hours" in {
        assertInterval(Hour(Value(1)), SortedSet(1))
        assertInterval(Hour(Range(Value(1), Value(3))), SortedSet(1, 2, 3))
        assertInterval(Hour(Range(Value(3), Value(1))), SortedSet((3 to 23) ++ (0 to 1): _*))
        assertInterval(Hour(All), SortedSet(0 to 23: _*))
        assertInterval(Hour(Increment(Value(1), Value(3))), SortedSet(1 to 23 by 3: _*))
        assertInterval(Hour(Increment(Range(Value(1), Value(3)), Value(3))), SortedSet(1 to 3 by 3: _*))
        assertInterval(
          Hour(Increment(Range(Value(3), Value(1)), Value(3))),
          SortedSet((3 to 23 by 3) ++ (0 to 1 by 3): _*)
        )
        assertInterval(Hour(Increment(All, Value(3))), SortedSet(0 to 23 by 3: _*))
        assertInterval(Hour(Increment(Value(3))), SortedSet(0 to 23 by 3: _*))
      }

      "expand day of month" in {
        assertInterval(DayOfMonth(Value(1)), SortedSet(1))
        assertInterval(DayOfMonth(Range(Value(1), Value(3))), SortedSet(1, 2, 3))
        assertInterval(DayOfMonth(Range(Value(3), Value(1))), SortedSet((3 to 31) ++ (1 to 1): _*))
        assertInterval(DayOfMonth(All), SortedSet(1 to 31: _*))
        assertInterval(DayOfMonth(Increment(Value(1), Value(3))), SortedSet(1 to 31 by 3: _*))
        assertInterval(DayOfMonth(Increment(Range(Value(1), Value(3)), Value(3))), SortedSet(1 to 3 by 3: _*))
        assertInterval(DayOfMonth(
          Increment(Range(Value(3), Value(1)), Value(3))),
          SortedSet((3 to 31 by 3) ++ (1 to 1 by 3): _*)
        )
        assertInterval(DayOfMonth(Increment(All, Value(3))), SortedSet(1 to 31 by 3: _*))
        assertInterval(DayOfMonth(Increment(Value(3))), SortedSet(1 to 31 by 3: _*))
        assertInterval(DayOfMonth(NoValue), SortedSet.empty[Int])
        assertInterval(DayOfMonth(Last), SortedSet(31))
        assertInterval(DayOfMonth(LastOffset(Value(3))), SortedSet(28))
        assertInterval(DayOfMonth(LastOffset(Value(29))), SortedSet.empty[Int])(date(2018, 2, 28, 0, 0, 0))
        assertInterval(DayOfMonth(Weekday(Value(29))), SortedSet.empty[Int])(date(2018, 2, 28, 0, 0, 0))
        assertInterval(DayOfMonth(Weekday(Value(3))), SortedSet(3))
        assertInterval(DayOfMonth(Weekday(Value(6))), SortedSet(5))
        assertInterval(DayOfMonth(Weekday(Value(7))), SortedSet(8))
        assertInterval(DayOfMonth(Weekday(Value(30))), SortedSet(28))(date(2018, 9, 30, 0, 0, 0))
        assertInterval(DayOfMonth(Weekday(Value(1))), SortedSet(3))(date(2018, 12, 1, 0, 0, 0))
        assertInterval(DayOfMonth(LastWeekday), SortedSet(31))
        assertInterval(DayOfMonth(LastWeekday), SortedSet(30))(date(2018, 3, 1, 0, 0, 0))
      }

      "expand month" in {
        assertInterval(Month(Value(1)), SortedSet(1))
        assertInterval(Month(Range(Value(1), Value(3))), SortedSet(1, 2, 3))
        assertInterval(Month(Range(Value(3), Value(1))), SortedSet((3 to 12) :+ 1: _*))
        assertInterval(Month(All), SortedSet(1 to 12: _*))
        assertInterval(Month(Increment(Value(1), Value(3))), SortedSet(1 to 12 by 3: _*))
        assertInterval(Month(Increment(Range(Value(1), Value(3)), Value(3))), SortedSet(1 to 3 by 3: _*))
        assertInterval(
          Month(Increment(Range(Value(3), Value(1)), Value(3))),
          SortedSet((3 to 12 by 3) ++ (1 to 1 by 3): _*)
        )
        assertInterval(Month(Increment(All, Value(3))), SortedSet(1 to 12 by 3: _*))
        assertInterval(Month(Increment(Value(3))), SortedSet(1 to 12 by 3: _*))
      }

      "expand day of week" in {
        assertInterval(DayOfWeek(Value(1)), SortedSet(7, 14, 21, 28))
        assertInterval(
          DayOfWeek(Range(Value(1), Value(3))),
          SortedSet(1, 2, 7, 8, 9, 14, 15, 16, 21, 22, 23, 28, 29, 30)
        )
        assertInterval(DayOfWeek(Range(Value(3), Value(1))), SortedSet(1 to 31: _*) - (1, 8, 15, 22, 29))
        assertInterval(DayOfWeek(All), SortedSet(1 to 31: _*))
        assertInterval(
          DayOfWeek(Increment(Value(1), Value(3))),
          SortedSet(3, 6, 7, 10, 13, 14, 17, 20, 21, 24, 27, 28, 31)
        )
        assertInterval(DayOfWeek(Increment(Range(Value(1), Value(3)), Value(3))), SortedSet(7, 14, 21, 28))
        assertInterval(
          DayOfWeek(Increment(Range(Value(3), Value(1)), Value(3))),
          SortedSet(2, 5, 7, 9, 12, 14, 16, 19, 21, 23, 26, 28, 30)
        )
        assertInterval(DayOfWeek(Increment(All, Value(3))), SortedSet(3, 6, 7, 10, 13, 14, 17, 20, 21, 24, 27, 28, 31))
        assertInterval(DayOfWeek(Increment(Value(3))), SortedSet(3, 6, 7, 10, 13, 14, 17, 20, 21, 24, 27, 28, 31))
        assertInterval(DayOfWeek(NoValue), SortedSet.empty[Int])
        assertInterval(DayOfWeek(Last), SortedSet(6, 13, 20, 27))
        assertInterval(DayOfWeek(LastDayOfMonth(Value(3))), SortedSet(30))
        assertInterval(DayOfWeek(LastDayOfMonth(Value(7))), SortedSet(27))
        assertInterval(DayOfWeek(NthXDayOfMonth(Value(3), Value(3))), SortedSet(16))
      }

      "expand year" in {
        assertInterval(Year(Value(2015)), SortedSet(2015))
        assertInterval(Year(Range(Value(2015), Value(2018))), SortedSet(2015, 2016, 2017, 2018))
        assertInterval(Year(Range(Value(2018), Value(2015))), SortedSet((2018 to 2099) ++ (1979 to 2015): _*))
        assertInterval(Year(All), SortedSet(1979 to 2099: _*))
        assertInterval(Year(Increment(Value(2015), Value(3))), SortedSet(2015 to 2099 by 3: _*))
        assertInterval(Year(Increment(Range(Value(2015), Value(2018)), Value(3))), SortedSet(2015, 2018))
        assertInterval(
          Year(Increment(Range(Value(2018), Value(2015)), Value(3))),
          SortedSet((2018 to 2099 by 3) ++ (1979 to 2015 by 3): _*)
        )
        assertInterval(Year(Increment(All, Value(3))), SortedSet(1979 to 2099 by 3: _*))
        assertInterval(Year(Increment(Value(3))), SortedSet(1979 to 2099 by 3: _*))
      }
    }
  }

  private def date(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): DateTime = {
    new DateTime(year, month, day, hour, minute, second, 0)
  }
}
