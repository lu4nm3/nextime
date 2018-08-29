package nextime

trait MultiPartExpression extends Expression {
  def parts: List[PartExpression]

  def mkString: String = parts.map(_.mkString).mkString(",")
}
