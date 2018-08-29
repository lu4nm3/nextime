package foo

import nextime._

object App {


  def main(args: Array[String]): Unit = {
    //    implicit def valueToSecondPart(value: Value): SecondPart = Coproduct[SecondPart](v(sdf:Int): String

    val second = Second(3, 8)
    val minute = Minute(1, 6)
    val hour = Hour(2 -/- 3, /(2))
    val dayOfMonth = DayOfMonth(L, 3 W)
    val month = Month(APR)
    val dayOfWeek = DayOfWeek(?, TUE, 3.L, 2 -#- 5)
    val year = Year(*)

    Second(List(1, 2, 3))
    Minute(1, 2, 3, Range(1, 2))
    Hour(List(1, 2, 3))
    DayOfMonth(List[DayOfMonthPart](1, 2, 3, LW))
    Month(List(1, 2, 3))
    DayOfWeek(List[DayOfWeekPart](1, 2, 3, L))
    Year(List(1, 2, 3))

    val cron = Cron(
      second,
      minute,
      hour,
      dayOfMonth,
      month,
      dayOfWeek
    )


    //    val cronStr = cron.map(_.mkString)
    //    val nextTime = cron.map(_.next())


    //    val x = second.map { sec =>
    //      sec.parts.map { part =>
    //        part.at[SecondPart]
    //        typed[]
    //      }
    //    }

    val s = cron.map(_.second.mkString)
    val cronStr = cron.map(_.mkString)
//    println("sdf")

    val c2 = cron"3,8 1,6 2/3,/2 L 4 ?,2,3L"

    //    val l = List(Foo(1, "SAdf"), Foo(4, "32"), Bar(3), Foo(5, "bdfb"))
    //
    //    val ss = l.map(l => implicitly[Show[Super]].show(l))


    while(true){}
    println("BarRecsdfsdf")


  }
}
