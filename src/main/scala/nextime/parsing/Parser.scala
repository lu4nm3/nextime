package nextime
package parsing

import fastparse.all.{CharIn, P, Parser => FParser, _}
import fastparse.core.Parsed.{Failure, Success}

trait Parser {
  def cron(cronExpression: String): Either[Error, Cron] = {
    parser.parse(cronExpression) match {
      case Success(cron, _) => cron
      case Failure(_, _, _) => Left(Error("Invalid cron expression", Error("Incorrect syntax")))
    }
  }

  def second(secondExpression: String): Either[Error, Second] = {
    second.parse(secondExpression) match {
      case Success(sec, _) => sec
      case Failure(_, _, _) => Left(Error("Invalid second expression", Error("Incorrect syntax")))
    }
  }

  def minute(minuteExpression: String): Either[Error, Minute] = {
    minute.parse(minuteExpression) match {
      case Success(min, _) => min
      case Failure(_, _, _) => Left(Error("Invalid minute expression", Error("Incorrect syntax")))
    }
  }

  def hour(hourExpression: String): Either[Error, Hour] = {
    hour.parse(hourExpression) match {
      case Success(hr, _) => hr
      case Failure(_, _, _) => Left(Error("Invalid hour expression", Error("Incorrect syntax")))
    }
  }

  def dayOfMonth(dayOfMonthExpression: String): Either[Error, DayOfMonth] = {
    dayOfMonth.parse(dayOfMonthExpression) match {
      case Success(doM, _) => doM
      case Failure(_, _, _) => Left(Error("Invalid day of month expression", Error("Incorrect syntax")))
    }
  }

  def month(monthExpression: String): Either[Error, Month] = {
    month.parse(monthExpression) match {
      case Success(mon, _) => mon
      case Failure(_, _, _) => Left(Error("Invalid month expression", Error("Incorrect syntax")))
    }
  }

  def dayOfWeek(dayOfWeekExpression: String): Either[Error, DayOfWeek] = {
    dayOfWeek.parse(dayOfWeekExpression) match {
      case Success(doW, _) => doW
      case Failure(_, _, _) => Left(Error("Invalid day of week expression", Error("Incorrect syntax")))
    }
  }

  def year(yearExpression: String): Either[Error, Year] = {
    year.parse(yearExpression) match {
      case Success(yr, _) => yr
      case Failure(_, _, _) => Left(Error("Invalid year expression", Error("Incorrect syntax")))
    }
  }

  protected val all: FParser[All.type] = P("*").map(_ => All)
  protected val number: FParser[Int] = ("-".?.! ~ CharIn('0' to '9').rep(1).!).map {
    case (sign, num) if sign.nonEmpty => -num.toInt
    case (_, num) => num.toInt
  }
  protected val value: FParser[Value] = number.map(Value)
  protected val noValue: FParser[NoValue.type] = P("?").map(_ => NoValue)
  protected val nth: FParser[NthXDayOfMonth] = (value ~ "#" ~ value).map {
    case (day, occurrence) => NthXDayOfMonth(day, occurrence)
  }
  protected val weekday: FParser[Weekday] = (value ~ "W").map(Weekday)
  protected val last: FParser[Last.type] = P("L").map(_ => Last)
  protected val lastOffset: FParser[LastOffset] = ("L-" ~ value).map(LastOffset)
  protected val lastWeekday: FParser[LastWeekday.type] = P("LW").map(_ => LastWeekday)
  protected val lastDayOfMonth: FParser[LastDayOfMonth] = (value ~ "L").map(LastDayOfMonth)
  protected val range: FParser[Range] = (value ~ "-" ~ value).map { case (lower, upper) => Range(lower, upper) }
  protected val increment: FParser[Increment] = ((range | value | all).? ~ "/" ~ value).map {
    case (bound: Option[IncrementPart], inc) => Increment(bound, inc)
  }

  private val second: FParser[Either[Error, Second]] = {
    (increment | range | value | all)
      .rep(min = 1, sep = ",")
      .map(parts => Second(parts.toList))
  }
  private val minute: FParser[Either[Error, Minute]] = {
    (increment | range | value | all)
      .rep(min = 1, sep = ",")
      .map(parts => Minute(parts.toList))
  }
  private val hour: FParser[Either[Error, Hour]] = {
    (increment | range | value | all)
      .rep(min = 1, sep = ",")
      .map(parts => Hour(parts.toList))
  }
  private val dayOfMonth: FParser[Either[Error, DayOfMonth]] = {
    (increment | range | all | noValue | lastWeekday | lastOffset | weekday | last | value)
      .rep(min = 1, sep = ",")
      .map(parts => DayOfMonth(parts.toList))
  }
  private val month: FParser[Either[Error, Month]] = {
    (increment | range | value | all)
      .rep(min = 1, sep = ",")
      .map(parts => Month(parts.toList))
  }
  private val dayOfWeek: FParser[Either[Error, DayOfWeek]] = {
    (increment | range | all | noValue | nth | lastDayOfMonth | last | value)
      .rep(min = 1, sep = ",")
      .map(parts => DayOfWeek(parts.toList))
  }
  private val year: FParser[Either[Error, Year]] = {
    (increment | range | value | all)
      .rep(min = 1, sep = ",")
      .map(parts => Year(parts.toList))
  }

  private val parser1: FParser[Either[Error, Cron]] = {
    (minute ~ " " ~ hour ~ " " ~ dayOfMonth ~ " " ~ month ~ " " ~ dayOfWeek ~ End)
      .map {
        case (min, hr, doM, mon, doW) => Cron(min, hr, doM, mon, doW)
      }
  }

  private val parser2: FParser[Either[Error, Cron]] = {
    (second ~ " " ~ minute ~ " " ~ hour ~ " " ~ dayOfMonth ~ " " ~ month ~ " " ~ dayOfWeek ~ End)
      .map {
        case (sec, min, hr, doM, mon, doW) => Cron(sec, min, hr, doM, mon, doW)
      }
  }

  private val parser3: FParser[Either[Error, Cron]] = {
    (second ~ " " ~ minute ~ " " ~ hour ~ " " ~ dayOfMonth ~ " " ~ month ~ " " ~ dayOfWeek ~ " " ~ year ~ End)
      .map {
        case (sec, min, hr, doM, mon, doW, yr) => Cron(sec, min, hr, doM, mon, doW, yr)
      }
  }

  private val parser: FParser[Either[Error, Cron]] = parser1 | parser2 | parser3
}

object Parser extends Parser
