package nextime

import scala.language.implicitConversions

trait CronUnsafeInstantiators {

  def unsafe(cronExpression: String): Cron = Cron(cronExpression)

  def unsafe(minute: Either[Violation, Minute],
             hour: Either[Violation, Hour],
             dayOfMonth: Either[Violation, DayOfMonth],
             month: Either[Violation, Month],
             dayOfWeek: Either[Violation, DayOfWeek]): Cron = Cron(minute, hour, dayOfMonth, month, dayOfWeek)

  def unsafe(second: Either[Violation, Second],
             minute: Either[Violation, Minute],
             hour: Either[Violation, Hour],
             dayOfMonth: Either[Violation, DayOfMonth],
             month: Either[Violation, Month],
             dayOfWeek: Either[Violation, DayOfWeek]): Cron = Cron(second, minute, hour, dayOfMonth, month, dayOfWeek)

  def unsafe(second: Either[Violation, Second],
             minute: Either[Violation, Minute],
             hour: Either[Violation, Hour],
             dayOfMonth: Either[Violation, DayOfMonth],
             month: Either[Violation, Month],
             dayOfWeek: Either[Violation, DayOfWeek],
             year: Either[Violation, Year]): Cron = Cron(second, minute, hour, dayOfMonth, month, dayOfWeek, year)

  private implicit def toUnsafe[A](value: Either[Violation, A]): A = value match {
    case Right(v) => v
    case Left(violation) => throw violation
  }
}
