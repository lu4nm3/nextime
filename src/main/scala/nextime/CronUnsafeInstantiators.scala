package nextime

import nextime.implicits.EitherImplicits._
import scala.language.implicitConversions

trait CronUnsafeInstantiators {

  def unsafe(cronExpression: String): Cron = Cron(cronExpression)

  def unsafe(minute: Either[Error, Minute],
             hour: Either[Error, Hour],
             dayOfMonth: Either[Error, DayOfMonth],
             month: Either[Error, Month],
             dayOfWeek: Either[Error, DayOfWeek]): Cron = Cron(minute, hour, dayOfMonth, month, dayOfWeek)

  def unsafe(second: Either[Error, Second],
             minute: Either[Error, Minute],
             hour: Either[Error, Hour],
             dayOfMonth: Either[Error, DayOfMonth],
             month: Either[Error, Month],
             dayOfWeek: Either[Error, DayOfWeek]): Cron = Cron(second, minute, hour, dayOfMonth, month, dayOfWeek)

  def unsafe(second: Either[Error, Second],
             minute: Either[Error, Minute],
             hour: Either[Error, Hour],
             dayOfMonth: Either[Error, DayOfMonth],
             month: Either[Error, Month],
             dayOfWeek: Either[Error, DayOfWeek],
             year: Either[Error, Year]): Cron = Cron(second, minute, hour, dayOfMonth, month, dayOfWeek, year)
}
