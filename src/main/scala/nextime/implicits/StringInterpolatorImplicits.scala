package nextime
package implicits

trait StringInterpolatorImplicits {

  implicit class CronInterpolator(val sc: StringContext) {
    def cron(args: Any*): Either[Error, Cron] = Cron(sc.raw())

    def ucron(args: Any*): Cron = Cron.unsafe(sc.raw())
  }

  implicit class SecondInterpolator(val sc: StringContext) {
    def sec(args: Any*): Either[Error, Second] = Second(sc.raw())

    def usec(args: Any*): Second = Second.unsafe(sc.raw())
  }

  implicit class MinuteInterpolator(val sc: StringContext) {
    def min(args: Any*): Either[Error, Minute] = Minute(sc.raw())

    def umin(args: Any*): Minute = Minute.unsafe(sc.raw())
  }

  implicit class HourInterpolator(val sc: StringContext) {
    def hr(args: Any*): Either[Error, Hour] = Hour(sc.raw())

    def uhr(args: Any*): Hour = Hour.unsafe(sc.raw())
  }

  implicit class DayOfMonthInterpolator(val sc: StringContext) {
    def dom(args: Any*): Either[Error, DayOfMonth] = DayOfMonth(sc.raw())

    def udom(args: Any*): DayOfMonth = DayOfMonth.unsafe(sc.raw())
  }

  implicit class MonthInterpolator(val sc: StringContext) {
    def mon(args: Any*): Either[Error, Month] = Month(sc.raw())

    def umon(args: Any*): Month = Month.unsafe(sc.raw())
  }

  implicit class DayOfWeekInterpolator(val sc: StringContext) {
    def dow(args: Any*): Either[Error, DayOfWeek] = DayOfWeek(sc.raw())

    def udow(args: Any*): DayOfWeek = DayOfWeek.unsafe(sc.raw())
  }

  implicit class YearInterpolator(val sc: StringContext) {
    def yr(args: Any*): Either[Error, Year] = Year(sc.raw())

    def uyr(args: Any*): Year = Year.unsafe(sc.raw())
  }
}
