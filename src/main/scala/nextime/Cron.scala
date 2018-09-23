package nextime

import nextime.parsing.Parser
import nextime.time.{NextTime, PreviousTime}

import scala.language.implicitConversions

sealed abstract case class Cron(second: Second,
                                minute: Minute,
                                hour: Hour,
                                dayOfMonth: DayOfMonth,
                                month: Month,
                                dayOfWeek: DayOfWeek,
                                year: Year) extends Expression with CronLike with NextTime with PreviousTime

object Cron extends CronEitherInstantiators with CronUnsafeInstantiators {
  def apply(cronExpression: String): Either[Violation, Cron] = {
    Parser.parse(cronExpression)
  }

  private[nextime] def apply(minute: Minute,
                             hour: Hour,
                             dayOfMonth: DayOfMonth,
                             month: Month,
                             dayOfWeek: DayOfWeek): Either[Violation, Cron] = {
    createCron(
      innerMinute = minute,
      innerHour = hour,
      innerDayOfMonth = dayOfMonth,
      innerMonth = month,
      innerDayOfWeek = dayOfWeek
    )
  }

  private[nextime] def apply(second: Second,
                             minute: Minute,
                             hour: Hour,
                             dayOfMonth: DayOfMonth,
                             month: Month,
                             dayOfWeek: DayOfWeek): Either[Violation, Cron] = {
    createCron(
      innerSecond = Some(second),
      innerMinute = minute,
      innerHour = hour,
      innerDayOfMonth = dayOfMonth,
      innerMonth = month,
      innerDayOfWeek = dayOfWeek
    )
  }

  private[nextime] def apply(second: Second,
                             minute: Minute,
                             hour: Hour,
                             dayOfMonth: DayOfMonth,
                             month: Month,
                             dayOfWeek: DayOfWeek,
                             year: Year): Either[Violation, Cron] = {
    createCron(
      innerSecond = Some(second),
      innerMinute = minute,
      innerHour = hour,
      innerDayOfMonth = dayOfMonth,
      innerMonth = month,
      innerDayOfWeek = dayOfWeek,
      innerYear = Some(year)
    )
  }

  private def createCron(innerSecond: Option[Second] = None,
                         innerMinute: Minute,
                         innerHour: Hour,
                         innerDayOfMonth: DayOfMonth,
                         innerMonth: Month,
                         innerDayOfWeek: DayOfWeek,
                         innerYear: Option[Year] = None): Either[Violation, Cron] = {

    if (hasNoValue(innerDayOfMonth) ^ hasNoValue(innerDayOfWeek)) {
      val second = innerSecond.getOrElse(Second.unsafe(0))
      val year = innerYear.getOrElse(Year.unsafe(*))

      Right(
        new Cron(second, innerMinute, innerHour, innerDayOfMonth, innerMonth, innerDayOfWeek, year) {
          def mkString: String = {
            val secondStr = innerSecond.map(s => s"${s.mkString} ").getOrElse("")
            val yearStr = innerYear.map(y => s" ${y.mkString}").getOrElse("")
            s"$secondStr${minute.mkString} ${hour.mkString} ${dayOfMonth.mkString} ${month.mkString} ${dayOfWeek.mkString}$yearStr"
          }
        }
      )
    } else if (hasNoValue(innerDayOfMonth) && hasNoValue(innerDayOfWeek)) {
      Left(
        Violation(
          "Invalid cron expression",
          Violation("Day-of-month and day-of-week must not both specify \"no value\"")
        )
      )
    } else {
      Left(
        Violation(
          "Invalid cron expression",
          Violation("Only day-of-month or day-of-week must be specified but not both")
        )
      )
    }
  }

  private def hasNoValue(subExpression: MultipartExpression): Boolean = {
    subExpression.parts.exists {
      case NoValue => true
      case _ => false
    }
  }
}
