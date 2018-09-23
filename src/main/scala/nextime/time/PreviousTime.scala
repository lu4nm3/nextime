package nextime
package time

import implicits._
import intervals._
import org.joda.time.{DateTime, MutableDateTime}

trait PreviousTime extends CronLike {

  def previous(dateTime: DateTime = DateTime.now): Option[DateTime] = {
    import util.control.Breaks._

    implicit val newDateTime: MutableDateTime = dateTime.toMutableDateTime
    newDateTime.subtractSeconds(1)

    var gotOne = false
    var impossible = false

    while (!gotOne && !impossible) breakable {

      if (newDateTime.getYear < Year.bounds.lower) { // prevent endless loop...
        impossible = true
        break()
      }

      // second
      var seconds = newDateTime.getSecondOfMinute
      val matchingSeconds = second.interval.to(seconds)
      if (matchingSeconds.nonEmpty) {
        seconds = matchingSeconds.last
      } else {
        newDateTime.subtractMinutes(1)
        seconds = second.interval.last
      }
      newDateTime.setSecondOfMinute(seconds)


      // minute
      var minutes = newDateTime.getMinuteOfHour
      val matchingMinutes = minute.interval.to(minutes)
      if (matchingMinutes.nonEmpty) {
        minutes = matchingMinutes.last
      } else {
        minutes = minute.interval.last
        newDateTime.setSecondOfMinute(newDateTime.lastSecondOfMinute)
        newDateTime.setMinuteOfHour(minutes)
        newDateTime.subtractHours(1)
        break()
      }
      newDateTime.setMinuteOfHour(minutes)


      // hour
      var hours = newDateTime.getHourOfDay
      val matchingHours = hour.interval.to(hours)
      if (matchingHours.nonEmpty) {
        hours = matchingHours.last
      } else {
        hours = hour.interval.last
        newDateTime.setSecondOfMinute(newDateTime.lastSecondOfMinute)
        newDateTime.setMinuteOfHour(newDateTime.lastMinuteOfHour)
        newDateTime.subtractDays(1)
        newDateTime.setHourOfDay(hours)
        break()
      }
      newDateTime.setHourOfDay(hours)


      // day
      val currentDay = newDateTime.getDayOfMonth
      val currentMonth = newDateTime.getMonthOfYear
      var days = newDateTime.getDayOfMonth
      var months = newDateTime.getMonthOfYear
      val matchingDays = if (hasNoValue(dayOfMonth)) {
        dayOfWeek.interval.to(days)
      } else {
        dayOfMonth.interval.to(days)
      }
      val lastDay = newDateTime.lastDayOfMonth

      if (matchingDays.nonEmpty) {
        days = matchingDays.last

        if (days > lastDay) {
          days = newDateTime.lastDayOfMonth
          months -= 1
        }
      } else {
        days = newDateTime.lastDayOfMonth
        months -= 1
      }
      if (currentDay != days || currentMonth != months) {
        if (currentMonth != months) {
          newDateTime.subtractMonths(1)
          newDateTime.setDayOfMonth(newDateTime.lastDayOfMonth)
        } else {
          newDateTime.setDayOfMonth(days)
        }
        newDateTime.setHourOfDay(newDateTime.lastHourOfDay)
        newDateTime.setMinuteOfHour(newDateTime.lastMinuteOfHour)
        newDateTime.setSecondOfMinute(newDateTime.lastSecondOfMinute)
        break()
      }


      if (newDateTime.getYear < Year.bounds.lower) {
        impossible = true
        break()
      }


      // month
      val matchingMonths = month.interval.to(months)
      if (matchingMonths.nonEmpty) {
        if (months != matchingMonths.last) {
          months = matchingMonths.last
          newDateTime.setMonthOfYear(months)
          newDateTime.setDayOfMonth(newDateTime.lastDayOfMonth)
          newDateTime.setHourOfDay(newDateTime.lastHourOfDay)
          newDateTime.setMinuteOfHour(newDateTime.lastMinuteOfHour)
          newDateTime.setSecondOfMinute(newDateTime.lastSecondOfMinute)
          break()
        }
        months = matchingMonths.last
      } else {
        months = month.interval.last
        val year = newDateTime.getYear - 1
        newDateTime.setYear(year)
        newDateTime.setMonthOfYear(months)
        newDateTime.setDayOfMonth(newDateTime.lastDayOfMonth)
        newDateTime.setHourOfDay(newDateTime.lastHourOfDay)
        newDateTime.setMinuteOfHour(newDateTime.lastMinuteOfHour)
        newDateTime.setSecondOfMinute(newDateTime.lastSecondOfMinute)
        break()
      }
      newDateTime.setMonthOfYear(months)


      // year
      var years = newDateTime.getYear
      val matchingYears = year.interval.to(years)
      if (matchingYears.nonEmpty) {
        years = matchingYears.last
      } else {
        impossible = true
        break()
      }
      newDateTime.setYear(years)
      gotOne = true
    }

    if (!impossible) {
      Some(newDateTime.toDateTime)
    } else {
      None
    }
  }
}
