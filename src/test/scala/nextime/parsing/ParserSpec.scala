package nextime
package parsing

import fastparse.core.Parsed
import fastparse.core.Parsed.{Failure, Success}
import org.scalatest.Matchers._
import org.scalatest.WordSpec

import scala.language.{implicitConversions, postfixOps}

class ParserSpec extends WordSpec with Parser {
  "Parser" when {
    "parsing part subexpression" should {
      "value" in {
        val strExpr = "3"

        val cronExpr = value.parse(strExpr)

        assertResult(Value(3))(cronExpr.get.value)
      }

      "range" in {
        val strExpr = "3-7"

        val cronExpr = range.parse(strExpr)

        assertResult(Range(3, 7))(cronExpr.get.value)
      }

      "all" in {
        val strExpr = "*"

        val cronExpr = all.parse(strExpr)

        assertResult(All)(cronExpr.get.value)
      }

      "increment" in {
        val strExpr1 = "3/19"
        val strExpr2 = "/3"

        val cronExpr1 = increment.parse(strExpr1)
        val cronExpr2 = increment.parse(strExpr2)

        assertResult(Increment(3, 19))(cronExpr1.get.value)
        assertResult(Increment(3))(cronExpr2.get.value)
      }

      "no value" in {
        val strExpr = "?"

        val cronExpr = noValue.parse(strExpr)

        assertResult(NoValue)(cronExpr.get.value)
      }

      "last" in {
        val strExpr = "L"

        val cronExpr = last.parse(strExpr)

        assertResult(Last)(cronExpr.get.value)
      }

      "last day of month" in {
        val strExpr = "3L"

        val cronExpr = lastDayOfMonth.parse(strExpr)

        assertResult(LastDayOfMonth(3))(cronExpr.get.value)
      }

      "last offset" in {
        val strExpr = "L-3"

        val cronExpr = lastOffset.parse(strExpr)

        assertResult(LastOffset(3))(cronExpr.get.value)
      }

      "weekday" in {
        val strExpr = "3W"

        val cronExpr = weekday.parse(strExpr)

        assertResult(Weekday(3))(cronExpr.get.value)
      }

      "last weekday" in {
        val strExpr = "LW"

        val cronExpr = lastWeekday.parse(strExpr)

        assertResult(LastWeekday)(cronExpr.get.value)
      }

      "nth X day of month" in {
        val strExpr = "3#3"

        val cronExpr = nth.parse(strExpr)

        assertResult(NthXDayOfMonth(3, 3))(cronExpr.get.value)
      }
    }

    "parsing subexpressions" should {
      "second" in {
        val strExpr = "1,2,3,3-7,7-3,*,1/3,/3"

        val cronExpr = second.parse(strExpr)

        assert(cronExpr.isRight)
        cronExpr.right.get shouldBe a[Second]
        cronExpr.right.get.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
      }

      "minute" in {
        val strExpr = "1,2,3,3-7,7-3,*,1/3,/3"

        val cronExpr = minute.parse(strExpr)

        assert(cronExpr.isRight)
        cronExpr.right.get shouldBe a[Minute]
        cronExpr.right.get.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
      }

      "hour" in {
        val strExpr = "1,2,3,3-7,7-3,*,1/3,/3"

        val cronExpr = hour.parse(strExpr)

        assert(cronExpr.isRight)
        cronExpr.right.get shouldBe a[Hour]
        cronExpr.right.get.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
      }

      "day of month" in {
        val strExpr = "1,2,3,3-7,7-3,*,1/3,/3,?,L,L-3,3W,LW"

        val cronExpr = dayOfMonth.parse(strExpr)

        assert(cronExpr.isRight)
        cronExpr.right.get shouldBe a[DayOfMonth]
        cronExpr.right.get.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3),
          NoValue,
          Last,
          LastOffset(3),
          Weekday(3),
          LastWeekday
        )
      }

      "month" in {
        val strExpr = "1,2,3,3-7,7-3,*,1/3,/3"

        val cronExpr = month.parse(strExpr)

        assert(cronExpr.isRight)
        cronExpr.right.get shouldBe a[Month]
        cronExpr.right.get.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
      }

      "day of week" in {
        val strExpr = "1,2,3,3-7,7-3,*,1/3,/3,?,L,3L,3#3"

        val cronExpr = dayOfWeek.parse(strExpr)

        assert(cronExpr.isRight)
        cronExpr.right.get shouldBe a[DayOfWeek]
        cronExpr.right.get.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3),
          NoValue,
          Last,
          LastDayOfMonth(3),
          NthXDayOfMonth(3, 3)
        )
      }

      "year" in {
        val strExpr = "1991,1993,2002,1991-2002,2002-1991,*,1991/3,/3"

        val cronExpr = year.parse(strExpr)

        assert(cronExpr.isRight)
        cronExpr.right.get shouldBe a[Year]
        cronExpr.right.get.parts should contain allOf(
          Value(1991), Value(1993), Value(2002),
          Range(1991, 2002), Range(2002, 1991),
          All,
          Increment(1991, 3), Increment(3)
        )
      }
    }

    "parsing cron expressions" should {
      val second = "1,2,3,3-7,7-3,*,1/3,/3"
      val minute = "1,2,3,3-7,7-3,*,1/3,/3"
      val hour = "1,2,3,3-7,7-3,*,1/3,/3"
      val dayOfMonth = "1,2,3,3-7,7-3,*,1/3,/3,L,L-3,3W,LW"
      val month = "1,2,3,3-7,7-3,*,1/3,/3"
      val dayOfWeek = "1,2,3,3-7,7-3,*,1/3,/3,L,3L,3#3"
      val year = "1991,1993,2002,1991-2002,2002-1991,*,1991/3,/3"

      "simple" in {
        val cronExpr1 = Parser.parse(minute + " " + hour + " " + dayOfMonth + " " + month + " " + "?")
        val cronExpr2 = Parser.parse(minute + " " + hour + " " + "?" + " " + month + " " + dayOfWeek)

        assert(cronExpr1.isRight)
        cronExpr1.right.get.minute.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr1.right.get.hour.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr1.right.get.dayOfMonth.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3),
          Last,
          LastOffset(3),
          Weekday(3),
          LastWeekday
        )
        cronExpr1.right.get.month.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr1.right.get.dayOfWeek.parts should contain(
          NoValue
        )
        assert(cronExpr2.isRight)
        cronExpr2.right.get.minute.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr2.right.get.hour.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr2.right.get.dayOfMonth.parts should contain(
          NoValue
        )
        cronExpr2.right.get.month.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr2.right.get.dayOfWeek.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3),
          Last,
          LastDayOfMonth(3),
          NthXDayOfMonth(3, 3)
        )
      }

      "intermediate" in {
        val cronExpr1 = Parser.parse(second + " " + minute + " " + hour + " " + dayOfMonth + " " + month + " " + "?")
        val cronExpr2 = Parser.parse(second + " " + minute + " " + hour + " " + "?" + " " + month + " " + dayOfWeek)

        assert(cronExpr1.isRight)
        cronExpr1.right.get.second.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr1.right.get.minute.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr1.right.get.hour.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr1.right.get.dayOfMonth.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3),
          Last,
          LastOffset(3),
          Weekday(3),
          LastWeekday
        )
        cronExpr1.right.get.month.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr1.right.get.dayOfWeek.parts should contain(
          NoValue
        )
        assert(cronExpr2.isRight)
        cronExpr2.right.get.second.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr2.right.get.minute.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr2.right.get.hour.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr2.right.get.dayOfMonth.parts should contain(
          NoValue
        )
        cronExpr2.right.get.month.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr2.right.get.dayOfWeek.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3),
          Last,
          LastDayOfMonth(3),
          NthXDayOfMonth(3, 3)
        )
      }

      "advanced" in {
        val cronExpr1 = Parser.parse(second + " " + minute + " " + hour + " " + dayOfMonth + " " + month + " " + "?" + " " + year)
        val cronExpr2 = Parser.parse(second + " " + minute + " " + hour + " " + "?" + " " + month + " " + dayOfWeek + " " + year)

        assert(cronExpr1.isRight)
        cronExpr1.right.get.second.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr1.right.get.minute.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr1.right.get.hour.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr1.right.get.dayOfMonth.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3),
          Last,
          LastOffset(3),
          Weekday(3),
          LastWeekday
        )
        cronExpr1.right.get.month.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr1.right.get.dayOfWeek.parts should contain(
          NoValue
        )
        cronExpr1.right.get.year.parts should contain allOf(
          Value(1991), Value(1993), Value(2002),
          Range(1991, 2002), Range(2002, 1991),
          All,
          Increment(1991, 3), Increment(3)
        )
        assert(cronExpr2.isRight)
        cronExpr2.right.get.second.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr2.right.get.minute.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr2.right.get.hour.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr2.right.get.dayOfMonth.parts should contain(
          NoValue
        )
        cronExpr2.right.get.month.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3)
        )
        cronExpr2.right.get.dayOfWeek.parts should contain allOf(
          Value(1), Value(2), Value(3),
          Range(3, 7), Range(7, 3),
          All,
          Increment(1, 3), Increment(3),
          Last,
          LastDayOfMonth(3),
          NthXDayOfMonth(3, 3)
        )
        cronExpr2.right.get.year.parts should contain allOf(
          Value(1991), Value(1993), Value(2002),
          Range(1991, 2002), Range(2002, 1991),
          All,
          Increment(1991, 3), Increment(3)
        )
      }
    }
  }

  private implicit def parsedToCron[T](parsed: Parsed[Parser.SubExpression[T], Char, String]): Either[Violation, T] = {
    parsed match {
      case Success(expr, _) => expr
      case Failure(x, y, z) =>
        Left(Violation("Invalid cron expression", Violation("Incorrect cron syntax")))
    }
  }
}
