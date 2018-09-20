package nextime

import cats.Semigroupal
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}
import parsing.Parser
import time.{NextTime, PreviousTime}
import validation.Violation

import scala.language.implicitConversions

sealed abstract case class Cron(second: Second,
                                minute: Minute,
                                hour: Hour,
                                dayOfMonth: DayOfMonth,
                                month: Month,
                                dayOfWeek: DayOfWeek,
                                year: Year) extends Expression with CronLike with NextTime with PreviousTime

object Cron {
  type ValidatedMaybe[A] = ValidatedNel[Violation, A]

  def apply(cronExpression: String): Either[Violation, Cron] = {
    Parser.parse(cronExpression)
  }

  def apply(minute: Minute,
            hour: Hour,
            dayOfMonth: DayOfMonth,
            month: Month,
            dayOfWeek: DayOfWeek): Maybe[Cron] = {
    createCron(innerMinute = minute, innerHour = hour, innerDayOfMonth = dayOfMonth, innerMonth = month, innerDayOfWeek = dayOfWeek)
  }

  def apply(minute: Maybe[Minute],
            hour: Maybe[Hour],
            dayOfMonth: Maybe[DayOfMonth],
            month: Maybe[Month],
            dayOfWeek: Maybe[DayOfWeek]): Maybe[Cron] = {

    Semigroupal.map5[ValidatedMaybe, Minute, Hour, DayOfMonth, Month, DayOfWeek, Maybe[Cron]](
      minute,
      hour,
      dayOfMonth,
      month,
      dayOfWeek
    )((minute, hour, dayOfMonth, month, dayOfWeek) => apply(minute, hour, dayOfMonth, month, dayOfWeek))
      .leftMap(errors => Violation("Invalid cron expression", errors.toList))
      .toEither
      .flatMap(Predef.identity)
  }

  def apply(second: Second,
            minute: Minute,
            hour: Hour,
            dayOfMonth: DayOfMonth,
            month: Month,
            dayOfWeek: DayOfWeek): Maybe[Cron] = {

    createCron(
      innerSecond = Some(second),
      innerMinute = minute,
      innerHour = hour,
      innerDayOfMonth = dayOfMonth,
      innerMonth = month,
      innerDayOfWeek = dayOfWeek
    )
  }

  def apply(second: Maybe[Second],
            minute: Maybe[Minute],
            hour: Maybe[Hour],
            dayOfMonth: Maybe[DayOfMonth],
            month: Maybe[Month],
            dayOfWeek: Maybe[DayOfWeek]): Maybe[Cron] = {

    Semigroupal.map6[ValidatedMaybe, Second, Minute, Hour, DayOfMonth, Month, DayOfWeek, Maybe[Cron]](
      second,
      minute,
      hour,
      dayOfMonth,
      month,
      dayOfWeek
    )((second, minute, hour, dayOfMonth, month, dayOfWeek) => apply(second, minute, hour, dayOfMonth, month, dayOfWeek))
      .leftMap(errors => Violation("Invalid cron expression", errors.toList))
      .toEither
      .flatMap(Predef.identity)
  }

  def apply(second: Second,
            minute: Minute,
            hour: Hour,
            dayOfMonth: DayOfMonth,
            month: Month,
            dayOfWeek: DayOfWeek,
            year: Year): Maybe[Cron] = {

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

  def apply(second: Maybe[Second],
            minute: Maybe[Minute],
            hour: Maybe[Hour],
            dayOfMonth: Maybe[DayOfMonth],
            month: Maybe[Month],
            dayOfWeek: Maybe[DayOfWeek],
            year: Maybe[Year]): Maybe[Cron] = {

    Semigroupal.map7[ValidatedMaybe, Second, Minute, Hour, DayOfMonth, Month, DayOfWeek, Year, Maybe[Cron]](
      second,
      minute,
      hour,
      dayOfMonth,
      month,
      dayOfWeek,
      year
    )((second, minute, hour, dayOfMonth, month, dayOfWeek, year) => apply(second, minute, hour, dayOfMonth, month, dayOfWeek, year))
      .leftMap((errors: NonEmptyList[Violation]) => Violation("Invalid cron expression", errors.toList))
      .toEither
      .flatMap(Predef.identity)
  }

  private def createCron(innerSecond: Option[Second] = None,
                         innerMinute: Minute,
                         innerHour: Hour,
                         innerDayOfMonth: DayOfMonth,
                         innerMonth: Month,
                         innerDayOfWeek: DayOfWeek,
                         innerYear: Option[Year] = None): Maybe[Cron] = {

    if (hasNoValue(innerDayOfMonth) ^ hasNoValue(innerDayOfWeek)) {
      val second = innerSecond.getOrElse(Second.unsafe(Value(0)))
      val year = innerYear.getOrElse(Year.unsafe(All))

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
          Violation("""Day-of-month and day-of-week must not both specify "no value"""")
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

  private implicit def maybeToValidated[A](maybe: Maybe[A]): ValidatedMaybe[A] = maybe match {
    case Left(violation) => Invalid(NonEmptyList.of(violation))
    case Right(a) => Valid(a)
  }
}
