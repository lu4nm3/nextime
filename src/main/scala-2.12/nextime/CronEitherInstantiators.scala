package nextime

import cats.Semigroupal
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}

import scala.language.implicitConversions

trait CronEitherInstantiators {
  type ValidatedMaybe[A] = ValidatedNel[Error, A]

  def apply(minute: Either[Error, Minute],
            hour: Either[Error, Hour],
            dayOfMonth: Either[Error, DayOfMonth],
            month: Either[Error, Month],
            dayOfWeek: Either[Error, DayOfWeek]): Either[Error, Cron] = {

    Semigroupal.map5[ValidatedMaybe, Minute, Hour, DayOfMonth, Month, DayOfWeek, Either[Error, Cron]](
      minute,
      hour,
      dayOfMonth,
      month,
      dayOfWeek
    )((minute, hour, dayOfMonth, month, dayOfWeek) => Cron(minute, hour, dayOfMonth, month, dayOfWeek))
      .leftMap(errors => Error("Invalid cron expression", errors.toList))
      .toEither
      .flatMap(Predef.identity)
  }

  def apply(second: Either[Error, Second],
            minute: Either[Error, Minute],
            hour: Either[Error, Hour],
            dayOfMonth: Either[Error, DayOfMonth],
            month: Either[Error, Month],
            dayOfWeek: Either[Error, DayOfWeek]): Either[Error, Cron] = {

    Semigroupal.map6[ValidatedMaybe, Second, Minute, Hour, DayOfMonth, Month, DayOfWeek, Either[Error, Cron]](
      second,
      minute,
      hour,
      dayOfMonth,
      month,
      dayOfWeek
    )((second, minute, hour, dayOfMonth, month, dayOfWeek) => Cron(second, minute, hour, dayOfMonth, month, dayOfWeek))
      .leftMap(errors => Error("Invalid cron expression", errors.toList))
      .toEither
      .flatMap(Predef.identity)
  }

  def apply(second: Either[Error, Second],
            minute: Either[Error, Minute],
            hour: Either[Error, Hour],
            dayOfMonth: Either[Error, DayOfMonth],
            month: Either[Error, Month],
            dayOfWeek: Either[Error, DayOfWeek],
            year: Either[Error, Year]): Either[Error, Cron] = {

    Semigroupal.map7[ValidatedMaybe, Second, Minute, Hour, DayOfMonth, Month, DayOfWeek, Year, Either[Error, Cron]](
      second,
      minute,
      hour,
      dayOfMonth,
      month,
      dayOfWeek,
      year
    )((second, minute, hour, dayOfMonth, month, dayOfWeek, year) =>
      Cron(second, minute, hour, dayOfMonth, month, dayOfWeek, year)
    )
      .leftMap(errors => Error("Invalid cron expression", errors.toList))
      .toEither
      .flatMap(Predef.identity)
  }

  private implicit def maybeToValidated[A](maybe: Either[Error, A]): ValidatedMaybe[A] = maybe match {
    case Left(error) => Invalid(NonEmptyList.of(error))
    case Right(a) => Valid(a)
  }
}
