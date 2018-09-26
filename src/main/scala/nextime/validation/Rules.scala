package nextime
package validation

import scala.language.higherKinds

trait Rules {
  type Errors = Vector[Error]

  private def error(message: String, cause: String): Errors = {
    Vector(Error(message, Error(cause)))
  }

  private def aggregateErrors(message: String, causes: List[Error]): Errors = {
    Vector(causes match {
      case Nil => Error(message)
      case cause :: Nil => Error(message, cause)
      case cause1 :: cause2 :: Nil => Error(message, cause1, cause2)
      case cause1 :: cause2 :: restCauses => Error(message, cause1, cause2, restCauses: _*)
    })
  }

  implicit def valueRule(implicit bounds: Bounds): Rule[Value] = Rule.from(
    value => error(
      s"Numeric values must be between ${bounds.lower} and ${bounds.upper}",
      s"${value.mkString} is out of bounds"
    ),
    value => value.value >= bounds.lower && value.value <= bounds.upper
  )

  implicit def rangeRule(implicit bounds: Bounds): Rule[Range] = Rule { range =>
    valueRule.errors(range.lower) ++ valueRule.errors(range.upper) match {
      case errors if errors.nonEmpty => aggregateErrors(
        s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
        errors.toList.flatMap(_.causes)
      )
      case _ => Vector.empty[Error]
    }
  }

  implicit val allRule: Rule[All.type] = Rule.empty[All.type]

  implicit def incrementRule(implicit bounds: Bounds): Rule[Increment] = Rule { increment =>
    val incRule: Rule[Value] = Rule.from(
      inc => error(
        "Increment values must be non-negative",
        s"${inc.mkString} is negative"
      ),
      inc => inc.value > 0
    )

    val errors = increment match {
      case Increment(Some(bound), inc) => bound match {
        case value@Value(_) => valueRule.errors(value) ++ incRule.errors(inc)
        case range@Range(_, _) => rangeRule.errors(range) ++ incRule.errors(inc)
        case All => incRule.errors(inc)
      }
      case Increment(None, inc) => incRule.errors(inc)
    }

    errors match {
      case errors if errors.nonEmpty => aggregateErrors(
        "One or more components of the increment subexpression are invalid",
        errors.toList
      )
      case _ => Vector.empty[Error]
    }
  }

  implicit val noValueRule: Rule[NoValue.type] = Rule.empty[NoValue.type]

  implicit val lastRule: Rule[Last.type] = Rule.empty[Last.type]

  implicit val lastDayOfMonthRule: Rule[LastDayOfMonth] = Rule.from(
    last => error(
      "Day of the week values must be between 1 and 7",
      s"${last.value.mkString} is out of bounds"
    ),
    last => last.value >= 1 && last.value <= 7
  )

  implicit val lastOffsetRule: Rule[LastOffset] = Rule.from(
    lastOffset => error(
      "Offset value from last day of the month must be between 0 and 30",
      s"${lastOffset.value.mkString} is out of bounds"
    ),
    lastOffset => lastOffset.value >= 0 && lastOffset.value <= 30
  )

  implicit val weekdayRule: Rule[Weekday] = Rule.from(
    weekday => error(
      "Weekday values must be between 1 and 31",
      s"${weekday.value.mkString} is out of bounds"
    ),
    weekday => weekday.value >= 1 && weekday.value <= 31
  )

  implicit val lastWeekdayRule: Rule[LastWeekday.type] = Rule.empty[LastWeekday.type]

  implicit val nthXDayOfMonthRule: Rule[NthXDayOfMonth] = Rule { nth =>
    val preceding: Rule[NthXDayOfMonth] = Rule.from(
      nth => error(
        "Weekday values preceding '#' must be between 1 and 7",
        s"${nth.dayOfWeek.mkString} is out of bounds"
      ),
      nth => nth.dayOfWeek >= 1 && nth.dayOfWeek <= 7
    )

    val following: Rule[NthXDayOfMonth] = Rule.from(
      nth => error(
        "Weekday values following '#' must be between 1 and 5",
        s"${nth.occurrenceInMonth.mkString} is out of bounds"
      ),
      nth => nth.occurrenceInMonth >= 1 && nth.occurrenceInMonth <= 5
    )

    preceding.errors(nth) ++ following.errors(nth) match {
      case errors if errors.nonEmpty => aggregateErrors(
        "One or more components of the nth x day of the month subexpression are invalid",
        errors.toList
      )
      case _ => Vector.empty[Error]
    }
  }
}
