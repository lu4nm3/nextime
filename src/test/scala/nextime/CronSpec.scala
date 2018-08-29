package nextime

import org.joda.time.DateTime
import org.scalatest.WordSpec

class CronSpec extends WordSpec {
  val dateTime: DateTime = date(2018, 1, 1, 0, 0, 0)

  "Cron" when {
    "hours" should {
      "range" in {
        val cron = Cron("5 3 1-6 11 * ? *").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get
      }
    }

    "day-of-month" should {
      "values" in {
        val cron = Cron("5 3 1 2,4 * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 2, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 4, 1, 3, 5))(next2)
        assertResult(date(2018, 2, 2, 1, 3, 5))(next3)
        assertResult(date(2018, 2, 4, 1, 3, 5))(next4)
        assertResult(date(2018, 3, 2, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "range" in {
        val cron = Cron("5 3 1 2-4 * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 2, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 3, 1, 3, 5))(next2)
        assertResult(date(2018, 1, 4, 1, 3, 5))(next3)
        assertResult(date(2018, 2, 2, 1, 3, 5))(next4)
        assertResult(date(2018, 2, 3, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "range overlap" in {
        val cron = Cron("5 3 1 3-1 * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 1, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 3, 1, 3, 5))(next2)
        assertResult(date(2018, 1, 4, 1, 3, 5))(next3)
        assertResult(date(2018, 1, 5, 1, 3, 5))(next4)
        assertResult(date(2018, 1, 6, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "all" in {
        val cron = Cron("5 3 1 * * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get
        val next6 = cron.next(next5).get
        val next7 = cron.next(next6).get
        val next8 = cron.next(next7).get

        val prev1 = cron.previous(next8.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get
        val prev6 = cron.previous(prev5).get
        val prev7 = cron.previous(prev6).get
        val prev8 = cron.previous(prev7).get

        assertResult(date(2018, 1, 1, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 2, 1, 3, 5))(next2)
        assertResult(date(2018, 1, 3, 1, 3, 5))(next3)
        assertResult(date(2018, 1, 4, 1, 3, 5))(next4)
        assertResult(date(2018, 1, 5, 1, 3, 5))(next5)
        assertResult(date(2018, 1, 6, 1, 3, 5))(next6)
        assertResult(date(2018, 1, 7, 1, 3, 5))(next7)
        assertResult(date(2018, 1, 8, 1, 3, 5))(next8)

        assertResult(next1)(prev8)
        assertResult(next2)(prev7)
        assertResult(next3)(prev6)
        assertResult(next4)(prev5)
        assertResult(next5)(prev4)
        assertResult(next6)(prev3)
        assertResult(next7)(prev2)
        assertResult(next8)(prev1)
      }

      "increment" in {
        val cron = Cron("5 3 1 1/3 * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 1, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 4, 1, 3, 5))(next2)
        assertResult(date(2018, 1, 7, 1, 3, 5))(next3)
        assertResult(date(2018, 1, 10, 1, 3, 5))(next4)
        assertResult(date(2018, 1, 13, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "last" in {
        val cron = Cron("5 3 1 L * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 31, 1, 3, 5))(next1)
        assertResult(date(2018, 2, 28, 1, 3, 5))(next2)
        assertResult(date(2018, 3, 31, 1, 3, 5))(next3)
        assertResult(date(2018, 4, 30, 1, 3, 5))(next4)
        assertResult(date(2018, 5, 31, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "last offset" in {
        val cron = Cron("5 3 1 L-3 * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 28, 1, 3, 5))(next1)
        assertResult(date(2018, 2, 25, 1, 3, 5))(next2)
        assertResult(date(2018, 3, 28, 1, 3, 5))(next3)
        assertResult(date(2018, 4, 27, 1, 3, 5))(next4)
        assertResult(date(2018, 5, 28, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "weekday" in {
        val cron = Cron("5 3 1 3W * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 3, 1, 3, 5))(next1)
        assertResult(date(2018, 2, 2, 1, 3, 5))(next2)
        assertResult(date(2018, 3, 2, 1, 3, 5))(next3)
        assertResult(date(2018, 4, 3, 1, 3, 5))(next4)
        assertResult(date(2018, 5, 3, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "last weekday" in {
        val cron = Cron("5 3 1 LW * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 31, 1, 3, 5))(next1)
        assertResult(date(2018, 2, 28, 1, 3, 5))(next2)
        assertResult(date(2018, 3, 30, 1, 3, 5))(next3)
        assertResult(date(2018, 4, 30, 1, 3, 5))(next4)
        assertResult(date(2018, 5, 31, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }
    }

    "day-of-week" should {
      "values" in {
        val cron = Cron("5 3 1 ? * 2,4").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 1, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 3, 1, 3, 5))(next2)
        assertResult(date(2018, 1, 8, 1, 3, 5))(next3)
        assertResult(date(2018, 1, 10, 1, 3, 5))(next4)
        assertResult(date(2018, 1, 15, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "range" in {
        val cron = Cron("5 3 1 ? * 2-4").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 1, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 2, 1, 3, 5))(next2)
        assertResult(date(2018, 1, 3, 1, 3, 5))(next3)
        assertResult(date(2018, 1, 8, 1, 3, 5))(next4)
        assertResult(date(2018, 1, 9, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "range overlap" in {
        val cron = Cron("5 3 1 ? * 3-1").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 2, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 3, 1, 3, 5))(next2)
        assertResult(date(2018, 1, 4, 1, 3, 5))(next3)
        assertResult(date(2018, 1, 5, 1, 3, 5))(next4)
        assertResult(date(2018, 1, 6, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "all" in {
        val cron = Cron("5 3 1 ? * *").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get
        val next6 = cron.next(next5).get
        val next7 = cron.next(next6).get
        val next8 = cron.next(next7).get

        val prev1 = cron.previous(next8.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get
        val prev6 = cron.previous(prev5).get
        val prev7 = cron.previous(prev6).get
        val prev8 = cron.previous(prev7).get

        assertResult(date(2018, 1, 1, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 2, 1, 3, 5))(next2)
        assertResult(date(2018, 1, 3, 1, 3, 5))(next3)
        assertResult(date(2018, 1, 4, 1, 3, 5))(next4)
        assertResult(date(2018, 1, 5, 1, 3, 5))(next5)
        assertResult(date(2018, 1, 6, 1, 3, 5))(next6)
        assertResult(date(2018, 1, 7, 1, 3, 5))(next7)
        assertResult(date(2018, 1, 8, 1, 3, 5))(next8)

        assertResult(next1)(prev8)
        assertResult(next2)(prev7)
        assertResult(next3)(prev6)
        assertResult(next4)(prev5)
        assertResult(next5)(prev4)
        assertResult(next6)(prev3)
        assertResult(next7)(prev2)
        assertResult(next8)(prev1)
      }

      "increment" in {
        val cron = Cron("5 3 1 ? * 1/3").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 3, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 6, 1, 3, 5))(next2)
        assertResult(date(2018, 1, 7, 1, 3, 5))(next3)
        assertResult(date(2018, 1, 10, 1, 3, 5))(next4)
        assertResult(date(2018, 1, 13, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "last" in {
        val cron = Cron("5 3 1 ? * L").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 6, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 13, 1, 3, 5))(next2)
        assertResult(date(2018, 1, 20, 1, 3, 5))(next3)
        assertResult(date(2018, 1, 27, 1, 3, 5))(next4)
        assertResult(date(2018, 2, 3, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "last day of month" in {
        val cron = Cron("5 3 1 ? * 5L").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(next5.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 25, 1, 3, 5))(next1)
        assertResult(date(2018, 2, 22, 1, 3, 5))(next2)
        assertResult(date(2018, 3, 29, 1, 3, 5))(next3)
        assertResult(date(2018, 4, 26, 1, 3, 5))(next4)
        assertResult(date(2018, 5, 31, 1, 3, 5))(next5)

        assertResult(next1)(prev5)
        assertResult(next2)(prev4)
        assertResult(next3)(prev3)
        assertResult(next4)(prev2)
        assertResult(next5)(prev1)
      }

      "nth X day of month" in {
        val cron = Cron("5 3 1 ? * 3#4").right.get // every 4th Tues

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get
        val next6 = cron.next(next5).get
        val next7 = cron.next(next6).get
        val next8 = cron.next(next7).get
        val next9 = cron.next(next8).get
        val next10 = cron.next(next9).get
        val next11 = cron.next(next10).get
        val next12 = cron.next(next11).get

        val prev1 = cron.previous(next12.plusSeconds(1)).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get
        val prev6 = cron.previous(prev5).get
        val prev7 = cron.previous(prev6).get
        val prev8 = cron.previous(prev7).get
        val prev9 = cron.previous(prev8).get
        val prev10 = cron.previous(prev9).get
        val prev11 = cron.previous(prev10).get
        val prev12 = cron.previous(prev11).get

        assertResult(date(2018, 1, 23, 1, 3, 5))(next1)
        assertResult(date(2018, 2, 27, 1, 3, 5))(next2)
        assertResult(date(2018, 3, 27, 1, 3, 5))(next3)
        assertResult(date(2018, 4, 24, 1, 3, 5))(next4)
        assertResult(date(2018, 5, 22, 1, 3, 5))(next5)
        assertResult(date(2018, 6, 26, 1, 3, 5))(next6)
        assertResult(date(2018, 7, 24, 1, 3, 5))(next7)
        assertResult(date(2018, 8, 28, 1, 3, 5))(next8)
        assertResult(date(2018, 9, 25, 1, 3, 5))(next9)
        assertResult(date(2018, 10, 23, 1, 3, 5))(next10)
        assertResult(date(2018, 11, 27, 1, 3, 5))(next11)
        assertResult(date(2018, 12, 25, 1, 3, 5))(next12)

        assertResult(next1)(prev12)
        assertResult(next2)(prev11)
        assertResult(next3)(prev10)
        assertResult(next4)(prev9)
        assertResult(next5)(prev8)
        assertResult(next6)(prev7)
        assertResult(next7)(prev6)
        assertResult(next8)(prev5)
        assertResult(next9)(prev4)
        assertResult(next10)(prev3)
        assertResult(next11)(prev2)
        assertResult(next12)(prev1)
      }
    }

    "valid" should {
      "pass the following test1" in {
        val cron = Cron("5,15 3,7 * 26 * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(dateTime).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 26, 0, 3, 5))(next1)
        assertResult(date(2018, 1, 26, 0, 3, 15))(next2)
        assertResult(date(2018, 1, 26, 0, 7, 5))(next3)
        assertResult(date(2018, 1, 26, 0, 7, 15))(next4)
        assertResult(date(2018, 1, 26, 1, 3, 5))(next5)

        assertResult(date(2017, 12, 26, 23, 7, 15))(prev1)
        assertResult(date(2017, 12, 26, 23, 7, 5))(prev2)
        assertResult(date(2017, 12, 26, 23, 3, 15))(prev3)
        assertResult(date(2017, 12, 26, 23, 3, 5))(prev4)
        assertResult(date(2017, 12, 26, 22, 7, 15))(prev5)
      }

      "pass the following test2" in {
        val cron = Cron("5 3 1 * * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(dateTime).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2018, 1, 1, 1, 3, 5))(next1)
        assertResult(date(2018, 1, 2, 1, 3, 5))(next2)
        assertResult(date(2018, 1, 3, 1, 3, 5))(next3)
        assertResult(date(2018, 1, 4, 1, 3, 5))(next4)
        assertResult(date(2018, 1, 5, 1, 3, 5))(next5)

        assertResult(date(2017, 12, 31, 1, 3, 5))(prev1)
        assertResult(date(2017, 12, 30, 1, 3, 5))(prev2)
        assertResult(date(2017, 12, 29, 1, 3, 5))(prev3)
        assertResult(date(2017, 12, 28, 1, 3, 5))(prev4)
        assertResult(date(2017, 12, 27, 1, 3, 5))(prev5)
      }

      "pass the following test3" in {
        val cron = Cron("5 3 1 LW * ? *").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get
        val next6 = cron.next(next5).get
        val next7 = cron.next(next6).get
        val next8 = cron.next(next7).get
        val next9 = cron.next(next8).get
        val next10 = cron.next(next9).get
        val next11 = cron.next(next10).get
        val next12 = cron.next(next11).get
        val next13 = cron.next(next12).get

        val prev1 = cron.previous(dateTime).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get
        val prev6 = cron.previous(prev5).get
        val prev7 = cron.previous(prev6).get
        val prev8 = cron.previous(prev7).get
        val prev9 = cron.previous(prev8).get
        val prev10 = cron.previous(prev9).get
        val prev11 = cron.previous(prev10).get
        val prev12 = cron.previous(prev11).get
        val prev13 = cron.previous(prev12).get

        assertResult(date(2018, 1, 31, 1, 3, 5))(next1)
        assertResult(date(2018, 2, 28, 1, 3, 5))(next2)
        assertResult(date(2018, 3, 30, 1, 3, 5))(next3)
        assertResult(date(2018, 4, 30, 1, 3, 5))(next4)
        assertResult(date(2018, 5, 31, 1, 3, 5))(next5)
        assertResult(date(2018, 6, 29, 1, 3, 5))(next6)
        assertResult(date(2018, 7, 31, 1, 3, 5))(next7)
        assertResult(date(2018, 8, 31, 1, 3, 5))(next8)
        assertResult(date(2018, 9, 28, 1, 3, 5))(next9)
        assertResult(date(2018, 10, 31, 1, 3, 5))(next10)
        assertResult(date(2018, 11, 30, 1, 3, 5))(next11)
        assertResult(date(2018, 12, 31, 1, 3, 5))(next12)
        assertResult(date(2019, 1, 31, 1, 3, 5))(next13)

        assertResult(date(2017, 12, 29, 1, 3, 5))(prev1)
        assertResult(date(2017, 11, 30, 1, 3, 5))(prev2)
        assertResult(date(2017, 10, 31, 1, 3, 5))(prev3)
        assertResult(date(2017, 9, 29, 1, 3, 5))(prev4)
        assertResult(date(2017, 8, 31, 1, 3, 5))(prev5)
        assertResult(date(2017, 7, 31, 1, 3, 5))(prev6)
        assertResult(date(2017, 6, 30, 1, 3, 5))(prev7)
        assertResult(date(2017, 5, 31, 1, 3, 5))(prev8)
        assertResult(date(2017, 4, 28, 1, 3, 5))(prev9)
        assertResult(date(2017, 3, 31, 1, 3, 5))(prev10)
        assertResult(date(2017, 2, 28, 1, 3, 5))(prev11)
        assertResult(date(2017, 1, 31, 1, 3, 5))(prev12)
        assertResult(date(2016, 12, 30, 1, 3, 5))(prev13)
      }

      "pass the following test4" in {
        val cron = Cron("5 3 1 L * ? *").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get
        val next6 = cron.next(next5).get
        val next7 = cron.next(next6).get
        val next8 = cron.next(next7).get
        val next9 = cron.next(next8).get
        val next10 = cron.next(next9).get
        val next11 = cron.next(next10).get
        val next12 = cron.next(next11).get
        val next13 = cron.next(next12).get

        val prev1 = cron.previous(dateTime).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get
        val prev6 = cron.previous(prev5).get
        val prev7 = cron.previous(prev6).get
        val prev8 = cron.previous(prev7).get
        val prev9 = cron.previous(prev8).get
        val prev10 = cron.previous(prev9).get
        val prev11 = cron.previous(prev10).get
        val prev12 = cron.previous(prev11).get
        val prev13 = cron.previous(prev12).get

        assertResult(date(2018, 1, 31, 1, 3, 5))(next1)
        assertResult(date(2018, 2, 28, 1, 3, 5))(next2)
        assertResult(date(2018, 3, 31, 1, 3, 5))(next3)
        assertResult(date(2018, 4, 30, 1, 3, 5))(next4)
        assertResult(date(2018, 5, 31, 1, 3, 5))(next5)
        assertResult(date(2018, 6, 30, 1, 3, 5))(next6)
        assertResult(date(2018, 7, 31, 1, 3, 5))(next7)
        assertResult(date(2018, 8, 31, 1, 3, 5))(next8)
        assertResult(date(2018, 9, 30, 1, 3, 5))(next9)
        assertResult(date(2018, 10, 31, 1, 3, 5))(next10)
        assertResult(date(2018, 11, 30, 1, 3, 5))(next11)
        assertResult(date(2018, 12, 31, 1, 3, 5))(next12)
        assertResult(date(2019, 1, 31, 1, 3, 5))(next13)

        assertResult(date(2017, 12, 31, 1, 3, 5))(prev1)
        assertResult(date(2017, 11, 30, 1, 3, 5))(prev2)
        assertResult(date(2017, 10, 31, 1, 3, 5))(prev3)
        assertResult(date(2017, 9, 30, 1, 3, 5))(prev4)
        assertResult(date(2017, 8, 31, 1, 3, 5))(prev5)
        assertResult(date(2017, 7, 31, 1, 3, 5))(prev6)
        assertResult(date(2017, 6, 30, 1, 3, 5))(prev7)
        assertResult(date(2017, 5, 31, 1, 3, 5))(prev8)
        assertResult(date(2017, 4, 30, 1, 3, 5))(prev9)
        assertResult(date(2017, 3, 31, 1, 3, 5))(prev10)
        assertResult(date(2017, 2, 28, 1, 3, 5))(prev11)
        assertResult(date(2017, 1, 31, 1, 3, 5))(prev12)
        assertResult(date(2016, 12, 31, 1, 3, 5))(prev13)
      }

      "pass the following test5" in {
        val cron = Cron("5 3 1 29 2 ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get

        val prev1 = cron.previous(dateTime).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get

        assertResult(date(2020, 2, 29, 1, 3, 5))(next1)
        assertResult(date(2024, 2, 29, 1, 3, 5))(next2)
        assertResult(date(2028, 2, 29, 1, 3, 5))(next3)
        assertResult(date(2032, 2, 29, 1, 3, 5))(next4)
        assertResult(date(2036, 2, 29, 1, 3, 5))(next5)

        assertResult(date(2016, 2, 29, 1, 3, 5))(prev1)
        assertResult(date(2012, 2, 29, 1, 3, 5))(prev2)
        assertResult(date(2008, 2, 29, 1, 3, 5))(prev3)
        assertResult(date(2004, 2, 29, 1, 3, 5))(prev4)
        assertResult(date(2000, 2, 29, 1, 3, 5))(prev5)
      }

      "pass the following test6" in {
        val cron = Cron("5 3 1 31 * ?").right.get

        val next1 = cron.next(dateTime).get
        val next2 = cron.next(next1).get
        val next3 = cron.next(next2).get
        val next4 = cron.next(next3).get
        val next5 = cron.next(next4).get
        val next6 = cron.next(next5).get
        val next7 = cron.next(next6).get
        val next8 = cron.next(next7).get
        val next9 = cron.next(next8).get
        val next10 = cron.next(next9).get
        val next11 = cron.next(next10).get
        val next12 = cron.next(next11).get
        val next13 = cron.next(next12).get
        val next14 = cron.next(next13).get

        val prev1 = cron.previous(dateTime).get
        val prev2 = cron.previous(prev1).get
        val prev3 = cron.previous(prev2).get
        val prev4 = cron.previous(prev3).get
        val prev5 = cron.previous(prev4).get
        val prev6 = cron.previous(prev5).get
        val prev7 = cron.previous(prev6).get
        val prev8 = cron.previous(prev7).get
        val prev9 = cron.previous(prev8).get
        val prev10 = cron.previous(prev9).get
        val prev11 = cron.previous(prev10).get
        val prev12 = cron.previous(prev11).get
        val prev13 = cron.previous(prev12).get
        val prev14 = cron.previous(prev13).get

        assertResult(date(2018, 1, 31, 1, 3, 5))(next1)
        assertResult(date(2018, 3, 31, 1, 3, 5))(next2)
        assertResult(date(2018, 5, 31, 1, 3, 5))(next3)
        assertResult(date(2018, 7, 31, 1, 3, 5))(next4)
        assertResult(date(2018, 8, 31, 1, 3, 5))(next5)
        assertResult(date(2018, 10, 31, 1, 3, 5))(next6)
        assertResult(date(2018, 12, 31, 1, 3, 5))(next7)
        assertResult(date(2019, 1, 31, 1, 3, 5))(next8)
        assertResult(date(2019, 3, 31, 1, 3, 5))(next9)
        assertResult(date(2019, 5, 31, 1, 3, 5))(next10)
        assertResult(date(2019, 7, 31, 1, 3, 5))(next11)
        assertResult(date(2019, 8, 31, 1, 3, 5))(next12)
        assertResult(date(2019, 10, 31, 1, 3, 5))(next13)
        assertResult(date(2019, 12, 31, 1, 3, 5))(next14)

        assertResult(date(2017, 12, 31, 1, 3, 5))(prev1)
        assertResult(date(2017, 10, 31, 1, 3, 5))(prev2)
        assertResult(date(2017, 8, 31, 1, 3, 5))(prev3)
        assertResult(date(2017, 7, 31, 1, 3, 5))(prev4)
        assertResult(date(2017, 5, 31, 1, 3, 5))(prev5)
        assertResult(date(2017, 3, 31, 1, 3, 5))(prev6)
        assertResult(date(2017, 1, 31, 1, 3, 5))(prev7)
        assertResult(date(2016, 12, 31, 1, 3, 5))(prev8)
        assertResult(date(2016, 10, 31, 1, 3, 5))(prev9)
        assertResult(date(2016, 8, 31, 1, 3, 5))(prev10)
        assertResult(date(2016, 7, 31, 1, 3, 5))(prev11)
        assertResult(date(2016, 5, 31, 1, 3, 5))(prev12)
        assertResult(date(2016, 3, 31, 1, 3, 5))(prev13)
        assertResult(date(2016, 1, 31, 1, 3, 5))(prev14)
      }

      "pass the following test7" in {
        val cron1 = Cron(Second(5), Minute(3), Hour(1), DayOfMonth(29), Month(3), DayOfWeek(?), Year(*))
        val cron2 = Cron(Second(5), Minute(3), Hour(1), DayOfMonth(29), Month(3), DayOfWeek(?))
        val cron3 = Cron(Minute(3), Hour(1), DayOfMonth(29), Month(2), DayOfWeek(?))

        assertResult("5 3 1 29 3 ? *")(cron1.right.get.mkString)
        assertResult("5 3 1 29 3 ?")(cron2.right.get.mkString)
        assertResult("3 1 29 2 ?")(cron3.right.get.mkString)
      }

      "failures" in {
//        val cron1 = Cron(Second(-5, -3), Minute(-3,-1, Range(3, -2)), Hour(-1, Range(-2, -3), Range(-4, -5)), DayOfMonth(-29), Month(3), DayOfWeek(?), Year(*))
        val cron2 = Cron(Second(-4), Minute(3), Hour(1), DayOfMonth(29), Month(-3), DayOfWeek(?))
        val cron3 = Cron(Minute(-3, -4), Hour(1), DayOfMonth(29), Month(2), DayOfWeek(-5, Increment(-3, -4), Range(-1, -8), NthXDayOfMonth(-3, -4)))

//        val cron1Err = cron1.left.get.mkString
        val cron2Err = cron2.left.get.mkString
        val cron3Err = cron3.left.get.mkString

//        assertResult("5 3 1 29 3 ? *")(cron1.right.get.mkString)
//        assertResult("5 3 1 29 3 ?")(cron2.right.get.mkString)
//        assertResult("3 1 29 2 ?")(cron3.right.get.mkString)
        assert(true)
      }
    }
  }

  private def date(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): DateTime = {
    new DateTime(year, month, day, hour, minute, second, 0)
  }

//  private implicit class DateTimeOps(dateTime: DateTime) {
//    def equals(other: DateTime): Boolean = {
//      dateTime.getYear == other.getYear &&
//      dateTime.getMonthOfYear == other.getMonthOfYear &&
//      dateTime.getDayOfMonth == other.getDayOfMonth &&
//      dateTime.getHourOfDay == other.getHourOfDay &&
//      dateTime.getMinuteOfHour == other.getMinuteOfHour &&
//      dateTime.getSecondOfMinute == other.getSecondOfMinute
//    }
//  }
}
