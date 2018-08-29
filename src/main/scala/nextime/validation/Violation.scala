package nextime
package validation

import io.circe.syntax._
import io.circe.{Encoder, Json}

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions

sealed trait Violation {
  def mkString: String = this.asJson.spaces4

  def msg: String

  def causes: List[Violation]
}

object Violation {

  case class Cause(message: String) extends Violation {
    def msg: String = message

    def causes: List[Violation] = List.empty[Violation]
  }

  case class UniqueViolation(message: String, cause: Violation) extends Violation {
    def msg: String = message

    def causes: List[Violation] = List(cause)
  }

  case class AggregateViolation(message: String,
                                firstCause: Violation,
                                secondCause: Violation,
                                remainingCauses: Violation*) extends Violation {
    def msg: String = message

    def causes: List[Violation] = firstCause +: secondCause +: remainingCauses.toList
  }

  def apply(message: String, causes: List[Violation]): Violation = {
    causes match {
      case Nil => Violation(message)
      case cause :: Nil => Violation(message, cause)
      case cause1 :: cause2 :: Nil => Violation(message, cause1, cause2)
      case cause1 :: cause2 :: restCauses => Violation(message, cause1, cause2, restCauses: _*)
    }
  }

  def apply(cause: String): Violation = Cause(cause)

  def apply(message: String, cause: Violation): Violation = UniqueViolation(message, cause)

  def apply(message: String, firstCause: Violation, secondCause: Violation, remainingCauses: Violation*): Violation = {
    case class Ctx(parentMsg: String, aggMsg: String, viols: List[Violation])

    def combineCtxs(ctxs: List[Ctx]): List[Violation] = {
      ctxs.map {
        case Ctx(_, aggMsg, violations) => violations match {
          case Nil => Cause(aggMsg)
          case v :: Nil => UniqueViolation(aggMsg, v)
          case v1 :: v2 :: Nil => AggregateViolation(aggMsg, v1, v2)
          case v1 :: v2 :: vs => AggregateViolation(aggMsg, v1, v2, vs: _*)
        }
      }
    }

    val causes: List[(String, List[Violation])] = {
      (firstCause +: secondCause +: remainingCauses.toList)
        .groupBy(_.msg)
        .toList
    }

    val tempStack = ListBuffer.empty[Ctx]
    val finalStack = ListBuffer.empty[Ctx]

    finalStack.+=:(Ctx("", message, List.empty[Violation]))
    causes.foreach { case (msg, violations) => tempStack.+=:(Ctx(message, msg, violations)) }

    while (tempStack.nonEmpty) {
      tempStack.remove(0) match {
        case Ctx(parentMsg, aggMsg, violations) =>
          violations
            .flatMap(viol => viol.causes) // List[Viol]
            .groupBy(_.msg) // Map[String, List[Viol]]
            .toList // List[(String, List[Viol])]
            .foreach { case (msg, viols) => tempStack.+=:(Ctx(aggMsg, msg, viols)) }

          finalStack.+=:(Ctx(parentMsg, aggMsg, List.empty[Violation]))
      }
    }

    while (finalStack.nonEmpty) {
      var head = finalStack.remove(0)

      if (tempStack.nonEmpty) {
        val ctxs = tempStack.filter(_.parentMsg == head.aggMsg).toList
        val violations = combineCtxs(ctxs)

        ctxs.foreach(ctx => tempStack -= ctx)
        tempStack.+=:(head.copy(viols = violations))
      } else {
        tempStack.+=:(head)
      }

      while (finalStack.nonEmpty && head.parentMsg == finalStack.head.parentMsg) {
        head = finalStack.remove(0)
        tempStack.+=:(head)
      }
    }

    combineCtxs(tempStack.toList).head
  }

  implicit val encodeViolation: Encoder[Violation] = new Encoder[Violation] {
    final def apply(violation: Violation): Json = violation match {
      case Cause(msg) => Json.fromString(msg)
      case v@UniqueViolation(_, _) =>
        Json.obj(
          ("message", Json.fromString(v.message)),
          ("cause", encodeViolation.apply(v.cause))
        )
      case a@AggregateViolation(_, _, _, _*) =>
        Json.obj(
          ("message", Json.fromString(a.message)),
          ("cause", Json.arr(a.causes.map(encodeViolation.apply): _*))
        )
    }
  }
}
