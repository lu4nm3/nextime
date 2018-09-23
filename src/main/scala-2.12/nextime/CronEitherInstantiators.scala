package nextime

import cats.Semigroupal
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}

import scala.language.implicitConversions

trait CronEitherInstantiators {
  type ValidatedMaybe[A] = ValidatedNel[Violation, A]

  def apply(minute: Either[Violation, Minute],
            hour: Either[Violation, Hour],
            dayOfMonth: Either[Violation, DayOfMonth],
            month: Either[Violation, Month],
            dayOfWeek: Either[Violation, DayOfWeek]): Either[Violation, Cron] = {

    Semigroupal.map5[ValidatedMaybe, Minute, Hour, DayOfMonth, Month, DayOfWeek, Either[Violation, Cron]](
      minute,
      hour,
      dayOfMonth,
      month,
      dayOfWeek
    )((minute, hour, dayOfMonth, month, dayOfWeek) => Cron(minute, hour, dayOfMonth, month, dayOfWeek))
      .leftMap(errors => Violation("Invalid cron expression", errors.toList))
      .toEither
      .flatMap(Predef.identity)
  }

  def apply(second: Either[Violation, Second],
            minute: Either[Violation, Minute],
            hour: Either[Violation, Hour],
            dayOfMonth: Either[Violation, DayOfMonth],
            month: Either[Violation, Month],
            dayOfWeek: Either[Violation, DayOfWeek]): Either[Violation, Cron] = {

    Semigroupal.map6[ValidatedMaybe, Second, Minute, Hour, DayOfMonth, Month, DayOfWeek, Either[Violation, Cron]](
      second,
      minute,
      hour,
      dayOfMonth,
      month,
      dayOfWeek
    )((second, minute, hour, dayOfMonth, month, dayOfWeek) => Cron(second, minute, hour, dayOfMonth, month, dayOfWeek))
      .leftMap(errors => Violation("Invalid cron expression", errors.toList))
      .toEither
      .flatMap(Predef.identity)
  }

  def apply(second: Either[Violation, Second],
            minute: Either[Violation, Minute],
            hour: Either[Violation, Hour],
            dayOfMonth: Either[Violation, DayOfMonth],
            month: Either[Violation, Month],
            dayOfWeek: Either[Violation, DayOfWeek],
            year: Either[Violation, Year]): Either[Violation, Cron] = {

    Semigroupal.map7[ValidatedMaybe, Second, Minute, Hour, DayOfMonth, Month, DayOfWeek, Year, Either[Violation, Cron]](
      second,
      minute,
      hour,
      dayOfMonth,
      month,
      dayOfWeek,
      year
    )((second, minute, hour, dayOfMonth, month, dayOfWeek, year) => Cron(second, minute, hour, dayOfMonth, month, dayOfWeek, year))
      .leftMap((errors: NonEmptyList[Violation]) => Violation("Invalid cron expression", errors.toList))
      .toEither
      .flatMap(Predef.identity)
  }

  private implicit def maybeToValidated[A](maybe: Either[Violation, A]): ValidatedMaybe[A] = maybe match {
    case Left(violation) => Invalid(NonEmptyList.of(violation))
    case Right(a) => Valid(a)
  }
}
