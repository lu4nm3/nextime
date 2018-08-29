package nextime
package validation

import org.scalatest.Matchers._
import org.scalatest.WordSpec

class RulesSpec extends WordSpec {

  implicit class Ops[T](value: T) {
    def validate(implicit rule: Rule[T]): Vector[Violation] = {
      rule.violations(value)
    }
  }

  "Rules" when {
    "checking subexpression part" should {
      "for value" should {
        implicit val bounds: Bounds = Bounds(1, 3)

        "be valid" in {
          val result1 = Value(1).validate
          val result2 = Value(2).validate
          val result3 = Value(3).validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
        }

        "be invalid" in {
          val result1 = Value(-1).validate
          val result2 = Value(4).validate

          assert(result1.nonEmpty)
          assertResult(
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds"))
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("4 is out of bounds"))
          )(result2.head)
        }
      }

      "for range" should {
        implicit val bounds: Bounds = Bounds(1, 5)

        "be valid" in {
          val result1 = Range(Value(1), Value(5)).validate
          val result2 = Range(Value(2), Value(4)).validate
          val result3 = Range(Value(1), Value(3)).validate
          val result4 = Range(Value(3), Value(5)).validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
          assert(result4.isEmpty)
        }

        "be invalid" in {
          val result1 = Range(Value(-1), Value(0)).validate
          val result2 = Range(Value(0), Value(1)).validate
          val result3 = Range(Value(5), Value(6)).validate
          val result4 = Range(Value(6), Value(7)).validate

          assert(result1.nonEmpty)
          assertResult(
            Violation(
              s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
              Violation("-1 is out of bounds"),
              Violation("0 is out of bounds")
            )
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation(
              s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
              Violation("0 is out of bounds")
            )
          )(result2.head)

          assert(result3.nonEmpty)
          assertResult(
            Violation(
              s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
              Violation("6 is out of bounds")
            )
          )(result3.head)

          assert(result4.nonEmpty)
          assertResult(
            Violation(
              s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
              Violation("6 is out of bounds"),
              Violation("7 is out of bounds")
            )
          )(result4.head)
        }
      }

      "for all" should {
        "always be valid" in {
          val result = All.validate

          assert(result.isEmpty)
        }
      }

      "for increment" should {
        implicit val bounds: Bounds = Bounds(1, 3)

        "be valid" in {
          val result1 = Increment(Value(2)).validate
          val result2 = Increment(Value(2), Value(2)).validate
          val result3 = Increment(Range(Value(1), Value(3)), Value(4)).validate
          val result4 = Increment(All, Value(3)).validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
          assert(result4.isEmpty)
        }

        "be invalid" in {
          val result1 = Increment(Value(-2)).validate
          val result2 = Increment(Value(-2), Value(2)).validate
          val result3 = Increment(Value(-2), Value(-2)).validate
          val result4 = Increment(Range(Value(0), Value(3)), Value(4)).validate
          val result5 = Increment(All, Value(-3)).validate

          assert(result1.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation("Increment values must be non-negative", Violation("-2 is negative"))
            )
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
            )
          )(result2.head)

          assert(result3.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation("Increment values must be non-negative", Violation("-2 is negative")),
              Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
            )
          )(result3.head)

          assert(result4.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation(
                s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
                Violation("0 is out of bounds")
              )
            )
          )(result4.head)

          assert(result5.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation("Increment values must be non-negative", Violation("-3 is negative"))
            )
          )(result5.head)
        }
      }

      "for no value" should {
        "always be valid" in {
          val result = NoValue.validate

          assert(result.isEmpty)
        }
      }

      "for last" should {
        "always be valid" in {
          val result = Last.validate

          assert(result.isEmpty)
        }
      }

      "for last day of month" should {
        "be valid" in {
          val result1 = LastDayOfMonth(Value(1)).validate
          val result2 = LastDayOfMonth(Value(3)).validate
          val result3 = LastDayOfMonth(Value(7)).validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
        }

        "be invalid" in {
          val result1 = LastDayOfMonth(Value(0)).validate
          val result2 = LastDayOfMonth(Value(8)).validate

          assert(result1.nonEmpty)
          assertResult(
            Violation("Day of the week values must be between 1 and 7", Violation("0 is out of bounds"))
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation("Day of the week values must be between 1 and 7", Violation("8 is out of bounds"))
          )(result2.head)
        }
      }

      "for last offset" should {
        "be valid" in {
          val result1 = LastOffset(Value(0)).validate
          val result2 = LastOffset(Value(15)).validate
          val result3 = LastOffset(Value(30)).validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
        }

        "be invalid" in {
          val result1 = LastOffset(Value(-1)).validate
          val result2 = LastOffset(Value(31)).validate

          assert(result1.nonEmpty)
          assertResult(
            Violation("Offset value from last day of the month must be between 0 and 30", Violation("-1 is out of bounds"))
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation("Offset value from last day of the month must be between 0 and 30", Violation("31 is out of bounds"))
          )(result2.head)
        }
      }

      "for weekday" should {
        "be valid" in {
          val result1 = Weekday(Value(1)).validate
          val result2 = Weekday(Value(15)).validate
          val result3 = Weekday(Value(31)).validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
        }

        "be invalid" in {
          val result1 = Weekday(Value(0)).validate
          val result2 = Weekday(Value(32)).validate

          assert(result1.nonEmpty)
          assertResult(
            Violation("Weekday values must be between 1 and 31", Violation("0 is out of bounds"))
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation("Weekday values must be between 1 and 31", Violation("32 is out of bounds"))
          )(result2.head)
        }
      }

      "for last weekday" should {
        "always be valid" in {
          val result = LastWeekday.validate

          assert(result.isEmpty)
        }
      }

      "for nth x day of month" should {
        "be valid" in {
          val result1 = NthXDayOfMonth(Value(1), Value(3)).validate
          val result2 = NthXDayOfMonth(Value(5), Value(3)).validate
          val result3 = NthXDayOfMonth(Value(7), Value(3)).validate
          val result4 = NthXDayOfMonth(Value(5), Value(1)).validate
          val result5 = NthXDayOfMonth(Value(5), Value(3)).validate
          val result6 = NthXDayOfMonth(Value(5), Value(5)).validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
          assert(result4.isEmpty)
          assert(result5.isEmpty)
          assert(result6.isEmpty)
        }

        "be invalid" in {
          val result1 = NthXDayOfMonth(Value(0), Value(3)).validate
          val result2 = NthXDayOfMonth(Value(8), Value(3)).validate
          val result3 = NthXDayOfMonth(Value(5), Value(0)).validate
          val result4 = NthXDayOfMonth(Value(5), Value(6)).validate
          val result5 = NthXDayOfMonth(Value(0), Value(6)).validate

          assert(result1.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the nth x day of the month subexpression are invalid",
              Violation("Weekday values preceding '#' must be between 1 and 7", Violation("0 is out of bounds"))
            )
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the nth x day of the month subexpression are invalid",
              Violation("Weekday values preceding '#' must be between 1 and 7", Violation("8 is out of bounds"))
            )
          )(result2.head)

          assert(result3.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the nth x day of the month subexpression are invalid",
              Violation("Weekday values following '#' must be between 1 and 5", Violation("0 is out of bounds"))
            )
          )(result3.head)

          assert(result4.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the nth x day of the month subexpression are invalid",
              Violation("Weekday values following '#' must be between 1 and 5", Violation("6 is out of bounds"))
            )
          )(result4.head)

          assert(result5.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the nth x day of the month subexpression are invalid",
              Violation("Weekday values following '#' must be between 1 and 5", Violation("6 is out of bounds")),
              Violation("Weekday values preceding '#' must be between 1 and 7", Violation("0 is out of bounds"))
            )
          )(result5.head)
        }
      }
    }

    "checking subexpression" should {
      "for second" should {
        implicit val bounds: Bounds = Bounds(1, 3)

        "be valid" in {
          val value: SecondPart = Value(1)
          val range: SecondPart = Range(Value(1), Value(3))
          val all: SecondPart = All
          val increment: SecondPart = Increment(Value(2), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = all.validate
          val result4 = increment.validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
          assert(result4.isEmpty)
        }

        "be invalid" in {
          val value: SecondPart = Value(-1)
          val range: SecondPart = Range(Value(-1), Value(3))
          val increment: SecondPart = Increment(Value(-2), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = increment.validate

          assert(result1.nonEmpty)
          assertResult(
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds"))
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation(
              s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
              Violation("-1 is out of bounds")
            )
          )(result2.head)

          assert(result3.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
            )
          )(result3.head)
        }
      }

      "for minute" should {
        implicit val bounds: Bounds = Bounds(1, 3)

        "be valid" in {
          val value: MinutePart = Value(1)
          val range: MinutePart = Range(Value(1), Value(3))
          val all: MinutePart = All
          val increment: MinutePart = Increment(Value(2), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = all.validate
          val result4 = increment.validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
          assert(result4.isEmpty)
        }

        "be invalid" in {
          val value: MinutePart = Value(-1)
          val range: MinutePart = Range(Value(-1), Value(3))
          val increment: MinutePart = Increment(Value(-2), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = increment.validate

          assert(result1.nonEmpty)
          assertResult(
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds"))
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation(
              s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
              Violation("-1 is out of bounds")
            )
          )(result2.head)

          assert(result3.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
            )
          )(result3.head)
        }
      }

      "for hour" should {
        implicit val bounds: Bounds = Bounds(1, 3)

        "be valid" in {
          val value: HourPart = Value(1)
          val range: HourPart = Range(Value(1), Value(3))
          val all: HourPart = All
          val increment: HourPart = Increment(Value(2), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = all.validate
          val result4 = increment.validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
          assert(result4.isEmpty)
        }

        "be invalid" in {
          val value: HourPart = Value(-1)
          val range: HourPart = Range(Value(-1), Value(3))
          val increment: HourPart = Increment(Value(-2), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = increment.validate

          assert(result1.nonEmpty)
          assertResult(
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds"))
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation(
              s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
              Violation("-1 is out of bounds")
            )
          )(result2.head)

          assert(result3.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
            )
          )(result3.head)
        }
      }

      "for day of month" should {
        implicit val bounds: Bounds = Bounds(1, 3)

        "be valid" in {
          val value: DayOfMonthPart = Value(1)
          val range: DayOfMonthPart = Range(Value(1), Value(3))
          val all: DayOfMonthPart = All
          val increment: DayOfMonthPart = Increment(Value(2), Value(3))
          val noValue: DayOfMonthPart = NoValue
          val last: DayOfMonthPart = Last
          val lastOffset: DayOfMonthPart = LastOffset(Value(2))
          val weekday: DayOfMonthPart = Weekday(Value(2))
          val lastWeekday: DayOfMonthPart = LastWeekday

          val result1 = value.validate
          val result2 = range.validate
          val result3 = all.validate
          val result4 = increment.validate
          val result5 = noValue.validate
          val result6 = last.validate
          val result7 = lastOffset.validate
          val result8 = weekday.validate
          val result9 = lastWeekday.validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
          assert(result4.isEmpty)
          assert(result5.isEmpty)
          assert(result6.isEmpty)
          assert(result7.isEmpty)
          assert(result8.isEmpty)
          assert(result9.isEmpty)
        }

        "be invalid" in {
          val value: DayOfMonthPart = Value(-1)
          val range: DayOfMonthPart = Range(Value(-1), Value(3))
          val increment: DayOfMonthPart = Increment(Value(-2), Value(3))
          val lastOffset: DayOfMonthPart = LastOffset(Value(-2))
          val weekday: DayOfMonthPart = Weekday(Value(-2))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = increment.validate
          val result4 = lastOffset.validate
          val result5 = weekday.validate

          assert(result1.nonEmpty)
          assertResult(
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds"))
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation(
              s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
              Violation("-1 is out of bounds")
            )
          )(result2.head)

          assert(result3.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
            )
          )(result3.head)

          assert(result4.nonEmpty)
          assertResult(
            Violation("Offset value from last day of the month must be between 0 and 30", Violation("-2 is out of bounds"))
          )(result4.head)

          assert(result5.nonEmpty)
          assertResult(
            Violation("Weekday values must be between 1 and 31", Violation("-2 is out of bounds"))
          )(result5.head)
        }
      }

      "for month" should {
        implicit val bounds: Bounds = Bounds(1, 3)

        "be valid" in {
          val value: MonthPart = Value(1)
          val range: MonthPart = Range(Value(1), Value(3))
          val all: MonthPart = All
          val increment: MonthPart = Increment(Value(2), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = all.validate
          val result4 = increment.validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
          assert(result4.isEmpty)
        }

        "be invalid" in {
          val value: MonthPart = Value(-1)
          val range: MonthPart = Range(Value(-1), Value(3))
          val increment: MonthPart = Increment(Value(-2), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = increment.validate

          assert(result1.nonEmpty)
          assertResult(
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds"))
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation(
              s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
              Violation("-1 is out of bounds")
            )
          )(result2.head)

          assert(result3.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
            )
          )(result3.head)
        }
      }

      "for day of week" should {
        implicit val bounds: Bounds = Bounds(1, 3)

        "be valid" in {
          val value: DayOfWeekPart = Value(1)
          val range: DayOfWeekPart = Range(Value(1), Value(3))
          val all: DayOfWeekPart = All
          val increment: DayOfWeekPart = Increment(Value(2), Value(3))
          val noValue: DayOfWeekPart = NoValue
          val last: DayOfWeekPart = Last
          val lastDayOfMonth: DayOfWeekPart = LastDayOfMonth(Value(2))
          val nthXDayOfMonth: DayOfWeekPart = NthXDayOfMonth(Value(3), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = all.validate
          val result4 = increment.validate
          val result5 = noValue.validate
          val result6 = last.validate
          val result7 = lastDayOfMonth.validate
          val result8 = nthXDayOfMonth.validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
          assert(result4.isEmpty)
          assert(result5.isEmpty)
          assert(result6.isEmpty)
          assert(result7.isEmpty)
          assert(result8.isEmpty)
        }

        "be invalid" in {
          val value: DayOfWeekPart = Value(-1)
          val range: DayOfWeekPart = Range(Value(-1), Value(3))
          val increment: DayOfWeekPart = Increment(Value(-2), Value(3))
          val lastDayOfMonth: DayOfWeekPart = LastDayOfMonth(Value(-2))
          val nthXDayOfMonth: DayOfWeekPart = NthXDayOfMonth(Value(-3), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = increment.validate
          val result4 = lastDayOfMonth.validate
          val result5 = nthXDayOfMonth.validate

          assert(result1.nonEmpty)
          assertResult(
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds"))
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation(
              s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
              Violation("-1 is out of bounds")
            )
          )(result2.head)

          assert(result3.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
            )
          )(result3.head)

          assert(result4.nonEmpty)
          assertResult(
            Violation("Day of the week values must be between 1 and 7", Violation("-2 is out of bounds"))
          )(result4.head)

          assert(result5.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the nth x day of the month subexpression are invalid",
              Violation("Weekday values preceding '#' must be between 1 and 7", Violation("-3 is out of bounds"))
            )
          )(result5.head)
        }
      }

      "for year" should {
        implicit val bounds: Bounds = Bounds(1, 3)

        "be valid" in {
          val value: YearPart = Value(1)
          val range: YearPart = Range(Value(1), Value(3))
          val all: YearPart = All
          val increment: YearPart = Increment(Value(2), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = all.validate
          val result4 = increment.validate

          assert(result1.isEmpty)
          assert(result2.isEmpty)
          assert(result3.isEmpty)
          assert(result4.isEmpty)
        }

        "be invalid" in {
          val value: YearPart = Value(-1)
          val range: YearPart = Range(Value(-1), Value(3))
          val increment: YearPart = Increment(Value(-2), Value(3))

          val result1 = value.validate
          val result2 = range.validate
          val result3 = increment.validate

          assert(result1.nonEmpty)
          assertResult(
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds"))
          )(result1.head)

          assert(result2.nonEmpty)
          assertResult(
            Violation(
              s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
              Violation("-1 is out of bounds")
            )
          )(result2.head)

          assert(result3.nonEmpty)
          assertResult(
            Violation(
              "One or more components of the increment subexpression are invalid",
              Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
            )
          )(result3.head)
        }
      }
    }

    "checking subexpression list" should {
      implicit val bounds: Bounds = Bounds(1, 3)

      "be valid" in {
        val value = Value(1)
        val range = Range(Value(1), Value(3))
        val all = All
        val increment = Increment(Value(2), Value(3))
        val noValue = NoValue
        val last = Last
        val lastDayOfMonth = LastDayOfMonth(Value(2))
        val lastOffset = LastOffset(Value(2))
        val weekday = Weekday(Value(2))
        val lastWeekday = LastWeekday
        val nthXDayOfMonth = NthXDayOfMonth(Value(2), Value(2))

        val secondParts: List[SecondPart] = List(value, range, all, increment)
        val minuteParts: List[MinutePart] = List(value, range, all, increment)
        val hourParts: List[HourPart] = List(value, range, all, increment)
        val dayOfMonthParts: List[DayOfMonthPart] = List(value, range, all, increment, noValue, last, lastOffset, weekday, lastWeekday)
        val monthParts: List[MonthPart] = List(value, range, all, increment)
        val dayOfWeekParts: List[DayOfWeekPart] = List(value, range, all, increment, noValue, last, lastDayOfMonth, nthXDayOfMonth)
        val yearParts: List[YearPart] = List(value, range, all, increment)

        val result1 = secondParts.flatMap(implicitly[Rule[SecondPart]].violations)
        val result2 = minuteParts.flatMap(implicitly[Rule[MinutePart]].violations)
        val result3 = hourParts.flatMap(implicitly[Rule[HourPart]].violations)
        val result4 = dayOfMonthParts.flatMap(implicitly[Rule[DayOfMonthPart]].violations)
        val result5 = monthParts.flatMap(implicitly[Rule[MonthPart]].violations)
        val result6 = dayOfWeekParts.flatMap(implicitly[Rule[DayOfWeekPart]].violations)
        val result7 = yearParts.flatMap(implicitly[Rule[YearPart]].violations)

        assert(result1.isEmpty)
        assert(result2.isEmpty)
        assert(result3.isEmpty)
        assert(result4.isEmpty)
        assert(result5.isEmpty)
        assert(result6.isEmpty)
        assert(result7.isEmpty)
      }

      "be invalid" in {
        val value = Value(-1)
        val range = Range(Value(-1), Value(-3))
        val increment = Increment(Value(-2), Value(-3))
        val lastDayOfMonth = LastDayOfMonth(Value(-2))
        val lastOffset = LastOffset(Value(-2))
        val weekday = Weekday(Value(-2))
        val nthXDayOfMonth = NthXDayOfMonth(Value(-2), Value(-3))

        val secondParts: List[SecondPart] = List(value, range, increment)
        val minuteParts: List[MinutePart] = List(value, range, increment)
        val hourParts: List[HourPart] = List(value, range, increment)
        val dayOfMonthParts: List[DayOfMonthPart] = List(value, range, increment, lastOffset, weekday)
        val monthParts: List[MonthPart] = List(value, range, increment)
        val dayOfWeekParts: List[DayOfWeekPart] = List(value, range, increment, lastDayOfMonth, nthXDayOfMonth)
        val yearParts: List[YearPart] = List(value, range, increment)

        val result1 = secondParts.flatMap(implicitly[Rule[SecondPart]].violations)
        val result2 = minuteParts.flatMap(implicitly[Rule[MinutePart]].violations)
        val result3 = hourParts.flatMap(implicitly[Rule[HourPart]].violations)
        val result4 = dayOfMonthParts.flatMap(implicitly[Rule[DayOfMonthPart]].violations)
        val result5 = monthParts.flatMap(implicitly[Rule[MonthPart]].violations)
        val result6 = dayOfWeekParts.flatMap(implicitly[Rule[DayOfWeekPart]].violations)
        val result7 = yearParts.flatMap(implicitly[Rule[YearPart]].violations)

        assert(result1.nonEmpty)
        result1 should contain allOf(
          Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds")),
          Violation(
            s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
            Violation("-1 is out of bounds"), Violation("-3 is out of bounds")
          ),
          Violation(
            "One or more components of the increment subexpression are invalid",
            Violation("Increment values must be non-negative", Violation("-3 is negative")),
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
          )
        )

        assert(result2.nonEmpty)
        result2 should contain allOf(
          Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds")),
          Violation(
            s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
            Violation("-1 is out of bounds"), Violation("-3 is out of bounds")
          ),
          Violation(
            "One or more components of the increment subexpression are invalid",
            Violation("Increment values must be non-negative", Violation("-3 is negative")),
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
          )
        )

        assert(result3.nonEmpty)
        result3 should contain allOf(
          Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds")),
          Violation(
            s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
            Violation("-1 is out of bounds"), Violation("-3 is out of bounds")
          ),
          Violation(
            "One or more components of the increment subexpression are invalid",
            Violation("Increment values must be non-negative", Violation("-3 is negative")),
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
          )
        )

        assert(result4.nonEmpty)
        result4 should contain allOf(
          Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds")),
          Violation(
            s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
            Violation("-1 is out of bounds"), Violation("-3 is out of bounds")
          ),
          Violation(
            "One or more components of the increment subexpression are invalid",
            Violation("Increment values must be non-negative", Violation("-3 is negative")),
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
          ),
          Violation("Offset value from last day of the month must be between 0 and 30", Violation("-2 is out of bounds")),
          Violation("Weekday values must be between 1 and 31", Violation("-2 is out of bounds"))
        )

        assert(result5.nonEmpty)
        result5 should contain allOf(
          Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds")),
          Violation(
            s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
            Violation("-1 is out of bounds"), Violation("-3 is out of bounds")
          ),
          Violation(
            "One or more components of the increment subexpression are invalid",
            Violation("Increment values must be non-negative", Violation("-3 is negative")),
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
          )
        )

        assert(result6.nonEmpty)
        result6 should contain allOf(
          Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds")),
          Violation(
            s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
            Violation("-1 is out of bounds"), Violation("-3 is out of bounds")
          ),
          Violation(
            "One or more components of the increment subexpression are invalid",
            Violation("Increment values must be non-negative", Violation("-3 is negative")),
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
          ),
          Violation("Day of the week values must be between 1 and 7", Violation("-2 is out of bounds")),
          Violation(
            "One or more components of the nth x day of the month subexpression are invalid",
            Violation("Weekday values following '#' must be between 1 and 5", Violation("-3 is out of bounds")),
            Violation("Weekday values preceding '#' must be between 1 and 7", Violation("-2 is out of bounds"))
          )
        )

        assert(result7.nonEmpty)
        result7 should contain allOf(
          Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-1 is out of bounds")),
          Violation(
            s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
            Violation("-1 is out of bounds"), Violation("-3 is out of bounds")
          ),
          Violation(
            "One or more components of the increment subexpression are invalid",
            Violation("Increment values must be non-negative", Violation("-3 is negative")),
            Violation(s"Numeric values must be between ${bounds.lower} and ${bounds.upper}", Violation("-2 is out of bounds"))
          )
        )
      }
    }
  }
}
