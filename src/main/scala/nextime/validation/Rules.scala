package nextime
package validation

import scala.language.higherKinds

trait Rules {
  type Violations = Vector[Violation]

  private def violation(message: String, cause: String): Violations = {
    Vector(Violation(message, Violation(cause)))
  }

  private def aggregateViolations(message: String, causes: List[Violation]): Violations = {
    Vector(causes match {
      case Nil => Violation(message)
      case cause :: Nil => Violation(message, cause)
      case cause1 :: cause2 :: Nil => Violation(message, cause1, cause2)
      case cause1 :: cause2 :: restCauses => Violation(message, cause1, cause2, restCauses: _*)
    })
  }

  implicit def valueRule(implicit bounds: Bounds): Rule[Value] = Rule.from(
    value => violation(
      s"Numeric values must be between ${bounds.lower} and ${bounds.upper}",
      s"${value.mkString} is out of bounds"
    ),
    value => value.value >= bounds.lower && value.value <= bounds.upper
  )

  implicit def rangeRule(implicit bounds: Bounds): Rule[Range] = Rule { range =>
    valueRule.violations(range.lower) ++ valueRule.violations(range.upper) match {
      case errors if errors.nonEmpty => aggregateViolations(
        s"Range lower and upper values must be between ${bounds.lower} and ${bounds.upper}",
        errors.toList.flatMap(_.causes)
      )
      case _ => Vector.empty[Violation]
    }
  }

  implicit val allRule: Rule[All.type] = Rule.empty[All.type]

  implicit def incrementRule(implicit bounds: Bounds): Rule[Increment] = Rule { increment =>
    val incRule: Rule[Value] = Rule.from(
      inc => violation(
        "Increment values must be non-negative",
        s"${inc.mkString} is negative"
      ),
      inc => inc.value > 0
    )

    val violations = increment match {
      case Increment(Some(bound), inc) => bound match {
        case value@Value(_) => valueRule.violations(value) ++ incRule.violations(inc)
        case range@Range(_, _) => rangeRule.violations(range) ++ incRule.violations(inc)
        case All => incRule.violations(inc)
      }
      case Increment(None, inc) => incRule.violations(inc)
    }

    violations match {
      case errors if errors.nonEmpty => aggregateViolations(
        "One or more components of the increment subexpression are invalid",
        errors.toList
      )
      case _ => Vector.empty[Violation]
    }
  }

  implicit val noValueRule: Rule[NoValue.type] = Rule.empty[NoValue.type]

  implicit val lastRule: Rule[Last.type] = Rule.empty[Last.type]

  implicit val lastDayOfMonthRule: Rule[LastDayOfMonth] = Rule.from(
    last => violation(
      "Day of the week values must be between 1 and 7",
      s"${last.value.mkString} is out of bounds"
    ),
    last => last.value >= 1 && last.value <= 7
  )

  implicit val lastOffsetRule: Rule[LastOffset] = Rule.from(
    lastOffset => violation(
      "Offset value from last day of the month must be between 0 and 30",
      s"${lastOffset.value.mkString} is out of bounds"
    ),
    lastOffset => lastOffset.value >= 0 && lastOffset.value <= 30
  )

  implicit val weekdayRule: Rule[Weekday] = Rule.from(
    weekday => violation(
      "Weekday values must be between 1 and 31",
      s"${weekday.value.mkString} is out of bounds"
    ),
    weekday => weekday.value >= 1 && weekday.value <= 31
  )

  implicit val lastWeekdayRule: Rule[LastWeekday.type] = Rule.empty[LastWeekday.type]

  implicit val nthXDayOfMonthRule: Rule[NthXDayOfMonth] = Rule { nth =>
    val preceding: Rule[NthXDayOfMonth] = Rule.from(
      nth => violation(
        "Weekday values preceding '#' must be between 1 and 7",
        s"${nth.dayOfWeek.mkString} is out of bounds"
      ),
      nth => nth.dayOfWeek >= 1 && nth.dayOfWeek <= 7
    )

    val following: Rule[NthXDayOfMonth] = Rule.from(
      nth => violation(
        "Weekday values following '#' must be between 1 and 5",
        s"${nth.occurrenceInMonth.mkString} is out of bounds"
      ),
      nth => nth.occurrenceInMonth >= 1 && nth.occurrenceInMonth <= 5
    )

    preceding.violations(nth) ++ following.violations(nth) match {
      case errors if errors.nonEmpty => aggregateViolations(
        "One or more components of the nth x day of the month subexpression are invalid",
        errors.toList
      )
      case _ => Vector.empty[Violation]
    }
  }
}
