package nextime

trait CronLike {
  def second: Second

  def minute: Minute

  def hour: Hour

  def dayOfMonth: DayOfMonth

  def month: Month

  def dayOfWeek: DayOfWeek

  def year: Year

  protected def hasNoValue(subExpression: MultiPartExpression): Boolean = {
    subExpression.parts.exists {
      case NoValue => true
      case _ => false
    }
  }
}
