package nextime

trait Parts {

  sealed trait PartExpression extends Expression

  sealed trait IncrementPart extends PartExpression

  sealed trait SecondPart extends PartExpression

  sealed trait MinutePart extends PartExpression

  sealed trait HourPart extends PartExpression

  sealed trait DayOfMonthPart extends PartExpression

  sealed trait MonthPart extends PartExpression

  sealed trait DayOfWeekPart extends PartExpression

  sealed trait YearPart extends PartExpression

  final case class Value(value: Int)
    extends IncrementPart
      with SecondPart
      with MinutePart
      with HourPart
      with DayOfMonthPart
      with MonthPart
      with DayOfWeekPart
      with YearPart {
    def mkString: String = value.toString
  }

  final case class Range(lower: Value,
                         upper: Value)
    extends IncrementPart
      with SecondPart
      with MinutePart
      with HourPart
      with DayOfMonthPart
      with MonthPart
      with DayOfWeekPart
      with YearPart {
    def mkString: String = s"${lower.mkString}-${upper.mkString}"
  }

  case object All
    extends IncrementPart
      with SecondPart
      with MinutePart
      with HourPart
      with DayOfMonthPart
      with MonthPart
      with DayOfWeekPart
      with YearPart {
    def mkString: String = "*"
  }

  final case class Increment(bound: Option[IncrementPart],
                             increment: Value)
    extends SecondPart
      with MinutePart
      with HourPart
      with DayOfMonthPart
      with MonthPart
      with DayOfWeekPart
      with YearPart {
    def mkString: String = s"${bound.map(_.mkString).getOrElse("")}/${increment.mkString}"
  }

  object Increment {
    def apply(bound: IncrementPart, increment: Value): Increment = Increment(Some(bound), increment)

    def apply(increment: Value): Increment = Increment(None, increment)
  }

  case object NoValue extends DayOfMonthPart with DayOfWeekPart {
    def mkString: String = "?"
  }

  case object Last extends DayOfMonthPart with DayOfWeekPart {
    def mkString: String = "L"
  }

  final case class LastDayOfMonth(value: Value) extends DayOfWeekPart {
    def mkString: String = s"${value.mkString}L"
  }

  final case class LastOffset(value: Value) extends DayOfMonthPart {
    def mkString: String = s"L-${value.mkString}"
  }

  final case class Weekday(value: Value) extends DayOfMonthPart {
    def mkString: String = s"${value.mkString}W"
  }

  case object LastWeekday extends DayOfMonthPart {
    def mkString: String = "LW"
  }

  final case class NthXDayOfMonth(dayOfWeek: Value, occurrenceInMonth: Value) extends DayOfWeekPart {
    def mkString: String = s"${dayOfWeek.mkString}#${occurrenceInMonth.mkString}"
  }

}
