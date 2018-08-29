package nextime
package time

import implicits._
import intervals._
import org.joda.time.{DateTime, MutableDateTime}

private[nextime] trait NextTime extends CronLike {

  def next(dateTime: DateTime = DateTime.now): Option[DateTime] = {
    import util.control.Breaks._

    implicit val newDateTime: MutableDateTime = dateTime.toMutableDateTime
    newDateTime.addSeconds(1)

    var gotOne = false
    var impossible = false

    while (!gotOne && !impossible) breakable {

      if (newDateTime.getYear > Year.bounds.upper) { // prevent endless loop...
        impossible = true
        break()
      }

      // second
      var seconds = newDateTime.getSecondOfMinute
      val matchingSeconds = second.interval.from(seconds)
      if (matchingSeconds.nonEmpty) {
        seconds = matchingSeconds.head
      } else {
        newDateTime.addMinutes(1)
        seconds = second.interval.head
      }
      newDateTime.setSecondOfMinute(seconds)


      // minute
      var minutes = newDateTime.getMinuteOfHour
      val matchingMinutes = minute.interval.from(minutes)
      if (matchingMinutes.nonEmpty) {
        minutes = matchingMinutes.head
      } else {
        minutes = minute.interval.head
        newDateTime.setSecondOfMinute(0)
        newDateTime.setMinuteOfHour(0)
        newDateTime.addHours(1)
        break()
      }
      newDateTime.setMinuteOfHour(minutes)


      // hour
      var hours = newDateTime.getHourOfDay
      val matchingHours = hour.interval.from(hours)
      if (matchingHours.nonEmpty) {
        hours = matchingHours.head
      } else {
        hours = hour.interval.head
        newDateTime.setSecondOfMinute(0)
        newDateTime.setMinuteOfHour(0)
        newDateTime.addDays(1)
        newDateTime.setHourOfDay(0)
        break()
      }
      newDateTime.setHourOfDay(hours)


      // day of month
      var days = newDateTime.getDayOfMonth
      val dayInterval = if (hasNoValue(dayOfMonth)) {
        dayOfWeek.interval
      } else {
        dayOfMonth.interval
      }
      val matchingDays = dayInterval.from(days)
      val lastDay = newDateTime.lastDayOfMonth

      if (matchingDays.nonEmpty) {
        days = matchingDays.head

        if (days > lastDay) {
          days = dayOfMonth.interval.head
          newDateTime.setSecondOfMinute(0)
          newDateTime.setMinuteOfHour(0)
          newDateTime.setHourOfDay(0)

          if (days > lastDay) {
            newDateTime.addDays(days - lastDay)
          } else {
            newDateTime.setDayOfMonth(days)
          }
          newDateTime.addMonths(1)
          break()
        }
      } else {
        days = dayInterval.head
        newDateTime.setSecondOfMinute(0)
        newDateTime.setMinuteOfHour(0)
        newDateTime.setHourOfDay(0)

        if (days > lastDay) {
          // add the difference in days which advances to the next month
          newDateTime.addDays(days - lastDay)
        } else {
          newDateTime.setDayOfMonth(1)
        }
        newDateTime.addMonths(1)
        break()
      }
      newDateTime.setDayOfMonth(days)


      // month
      if (newDateTime.getYear > Year.bounds.upper) {
        impossible = true
        break()
      }

      var months = newDateTime.getMonthOfYear
      val matchingMonths = month.interval.from(months)
      if (matchingMonths.nonEmpty) {
        if (months != matchingMonths.head) {
          months = matchingMonths.head
          newDateTime.setSecondOfMinute(0)
          newDateTime.setMinuteOfHour(0)
          newDateTime.setHourOfDay(0)
          newDateTime.setDayOfMonth(1)
          newDateTime.setMonthOfYear(months)
          break()
        }
        months = matchingMonths.head
      } else {
        months = month.interval.head
        newDateTime.setSecondOfMinute(0)
        newDateTime.setMinuteOfHour(0)
        newDateTime.setHourOfDay(0)
        newDateTime.setDayOfMonth(1)
        newDateTime.setMonthOfYear(1)
        newDateTime.addYears(1)
        break()
      }
      newDateTime.setMonthOfYear(months)


      // year
      var years = newDateTime.getYear
      val matchingYears = year.interval.from(years)
      if (matchingYears.nonEmpty) {
        years = matchingYears.head
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
