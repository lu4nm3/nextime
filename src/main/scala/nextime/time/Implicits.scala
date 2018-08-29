package nextime.time

import org.joda.time.{DateTime, MutableDateTime}

import scala.language.implicitConversions

private[nextime] trait Implicits {

  implicit def implMutableToDateTime(implicit m: MutableDateTime): DateTime = m.toDateTime

  implicit def mutableToDateTime(m: MutableDateTime): DateTime = m.toDateTime

  implicit class DateTimeOps[T](dateTime: T)(implicit f: T => DateTime) {
    def normalizedDayOfWeek: Int = dateTime.plusDays(1).getDayOfWeek

    def lastSecondOfMinute: Int = dateTime.secondOfMinute.getMaximumValue

    def lastMinuteOfHour: Int = dateTime.minuteOfHour.getMaximumValue

    def lastHourOfDay: Int = dateTime.hourOfDay.getMaximumValue

    def lastDayOfMonth: Int = dateTime.dayOfMonth.getMaximumValue
  }

  implicit class MutableDateTimeOps(mutableDateTime: MutableDateTime) {
    def subtractSeconds(seconds: Int): Unit = {
      val dateTime = mutableDateTime.minusSeconds(seconds)
      mutableDateTime.setYear(dateTime.getYear)
      mutableDateTime.setMonthOfYear(dateTime.getMonthOfYear)
      mutableDateTime.setDayOfMonth(dateTime.getDayOfMonth)
      mutableDateTime.setHourOfDay(dateTime.getHourOfDay)
      mutableDateTime.setMinuteOfHour(dateTime.getMinuteOfHour)
      mutableDateTime.setSecondOfMinute(dateTime.getSecondOfMinute)
    }

    def subtractMinutes(minutes: Int): Unit = {
      val dateTime = mutableDateTime.minusMinutes(minutes)
      mutableDateTime.setYear(dateTime.getYear)
      mutableDateTime.setMonthOfYear(dateTime.getMonthOfYear)
      mutableDateTime.setDayOfMonth(dateTime.getDayOfMonth)
      mutableDateTime.setHourOfDay(dateTime.getHourOfDay)
      mutableDateTime.setMinuteOfHour(dateTime.getMinuteOfHour)
    }

    def subtractHours(hours: Int): Unit = {
      val dateTime = mutableDateTime.minusHours(hours)
      mutableDateTime.setYear(dateTime.getYear)
      mutableDateTime.setMonthOfYear(dateTime.getMonthOfYear)
      mutableDateTime.setDayOfMonth(dateTime.getDayOfMonth)
      mutableDateTime.setHourOfDay(dateTime.getHourOfDay)
    }

    def subtractDays(days: Int): Unit = {
      val dateTime = mutableDateTime.minusDays(days)
      mutableDateTime.setYear(dateTime.getYear)
      mutableDateTime.setMonthOfYear(dateTime.getMonthOfYear)
      mutableDateTime.setDayOfMonth(dateTime.getDayOfMonth)
    }

    def subtractMonths(months: Int): Unit = {
      val dateTime = mutableDateTime.minusMonths(months)
      mutableDateTime.setYear(dateTime.getYear)
      mutableDateTime.setMonthOfYear(dateTime.getMonthOfYear)
    }
  }

}
