package nextime
package parsing

import fastparse.all.{CharIn, P, Parser => FParser, _}
import fastparse.core.Parsed.{Failure, Success}

trait Parser {
  def parse(cronExpression: String): Either[Error, Cron] = {
    parser.parse(cronExpression) match {
      case Success(cron, _) => cron
      case Failure(_, _, _) => Left(Error("Invalid cron expression", Error("Incorrect cron syntax")))
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

  type SubExpression[T] = Either[Error, T]

  protected val second: FParser[SubExpression[Second]] = {
    (increment | range | value | all)
      .rep(min = 1, sep = ",")
      .map(parts => Second(parts.toList))
  }
  protected val minute: FParser[SubExpression[Minute]] = {
    (increment | range | value | all)
      .rep(min = 1, sep = ",")
      .map(parts => Minute(parts.toList))
  }
  protected val hour: FParser[SubExpression[Hour]] = {
    (increment | range | value | all)
      .rep(min = 1, sep = ",")
      .map(parts => Hour(parts.toList))
  }
  protected val dayOfMonth: FParser[SubExpression[DayOfMonth]] = {
    (increment | range | all | noValue | lastWeekday | lastOffset | weekday | last | value)
      .rep(min = 1, sep = ",")
      .map(parts => DayOfMonth(parts.toList))
  }
  protected val month: FParser[SubExpression[Month]] = {
    (increment | range | value | all)
      .rep(min = 1, sep = ",")
      .map(parts => Month(parts.toList))
  }
  protected val dayOfWeek: FParser[SubExpression[DayOfWeek]] = {
    (increment | range | all | noValue | nth | lastDayOfMonth | last | value)
      .rep(min = 1, sep = ",")
      .map(parts => DayOfWeek(parts.toList))
  }
  protected val year: FParser[SubExpression[Year]] = {
    (increment | range | value | all)
      .rep(min = 1, sep = ",")
      .map(parts => Year(parts.toList))
  }

  protected val parser1: FParser[Either[Error, Cron]] = {
    (minute ~ " " ~ hour ~ " " ~ dayOfMonth ~ " " ~ month ~ " " ~ dayOfWeek ~ End)
      .map {
        case (min, hr, doM, mon, doW) => Cron(min, hr, doM, mon, doW)
      }
  }

  protected val parser2: FParser[Either[Error, Cron]] = {
    (second ~ " " ~ minute ~ " " ~ hour ~ " " ~ dayOfMonth ~ " " ~ month ~ " " ~ dayOfWeek ~ End)
      .map {
        case (sec, min, hr, doM, mon, doW) => Cron(sec, min, hr, doM, mon, doW)
      }
  }

  protected val parser3: FParser[Either[Error, Cron]] = {
    (second ~ " " ~ minute ~ " " ~ hour ~ " " ~ dayOfMonth ~ " " ~ month ~ " " ~ dayOfWeek ~ " " ~ year ~ End)
      .map {
        case (sec, min, hr, doM, mon, doW, yr) => Cron(sec, min, hr, doM, mon, doW, yr)
      }
  }

  protected val parser: FParser[Either[Error, Cron]] = parser1 | parser2 | parser3
}

object Parser extends Parser
