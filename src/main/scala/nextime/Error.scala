package nextime

import io.circe.syntax._
import io.circe.{Encoder, Json}

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions

sealed trait Error extends Throwable {
  def mkString: String = s"\n${this.asJson.spaces4}"

  def message: String

  def causes: List[Error]

  override def getMessage: String = mkString
}

object Error {

  case class Cause(msg: String) extends Error {
    def message: String = msg

    def causes: List[Error] = List.empty[Error]
  }

  case class UniqueError(msg: String, cause: Error) extends Error {
    def message: String = msg

    def causes: List[Error] = List(cause)
  }

  case class AggregateError(msg: String,
                            firstCause: Error,
                            secondCause: Error,
                            remainingCauses: Error*) extends Error {
    def message: String = msg

    def causes: List[Error] = firstCause +: secondCause +: remainingCauses.toList
  }

  def apply(message: String, causes: List[Error]): Error = {
    causes match {
      case Nil => Error(message)
      case cause :: Nil => Error(message, cause)
      case cause1 :: cause2 :: Nil => Error(message, cause1, cause2)
      case cause1 :: cause2 :: restCauses => Error(message, cause1, cause2, restCauses: _*)
    }
  }

  def apply(cause: String): Error = Cause(cause)

  def apply(message: String, cause: Error): Error = UniqueError(message, cause)

  def apply(message: String, firstCause: Error, secondCause: Error, remainingCauses: Error*): Error = {
    case class Ctx(parentMsg: String, aggMsg: String, errors: List[Error])

    def combineCtxs(ctxs: List[Ctx]): List[Error] = {
      ctxs.map {
        case Ctx(_, aggMsg, errors) => errors match {
          case Nil => Cause(aggMsg)
          case e :: Nil => UniqueError(aggMsg, e)
          case e1 :: e2 :: Nil => AggregateError(aggMsg, e1, e2)
          case e1 :: e2 :: es => AggregateError(aggMsg, e1, e2, es: _*)
        }
      }
    }

    val causes: List[(String, List[Error])] = {
      (firstCause +: secondCause +: remainingCauses.toList)
        .groupBy(_.message)
        .toList
    }

    val tempStack = ListBuffer.empty[Ctx]
    val finalStack = ListBuffer.empty[Ctx]

    finalStack.+=:(Ctx("", message, List.empty[Error]))
    causes.foreach { case (msg, errors) => tempStack.+=:(Ctx(message, msg, errors)) }

    while (tempStack.nonEmpty) {
      tempStack.remove(0) match {
        case Ctx(parentMsg, aggMsg, errors) =>
          errors
            .flatMap(error => error.causes) // List[Error]
            .groupBy(_.message) // Map[String, List[Error]]
            .toList // List[(String, List[Error])]
            .foreach { case (msg, errs) => tempStack.+=:(Ctx(aggMsg, msg, errs)) }

          finalStack.+=:(Ctx(parentMsg, aggMsg, List.empty[Error]))
      }
    }

    while (finalStack.nonEmpty) {
      var head = finalStack.remove(0)

      if (tempStack.nonEmpty) {
        val ctxs = tempStack.filter(_.parentMsg == head.aggMsg).toList
        val errors = combineCtxs(ctxs)

        ctxs.foreach(ctx => tempStack -= ctx)
        tempStack.+=:(head.copy(errors = errors))
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

  implicit val encodeCronError: Encoder[Error] = new Encoder[Error] {
    final def apply(error: Error): Json = error match {
      case Cause(msg) => Json.fromString(msg)
      case e@UniqueError(_, _) =>
        Json.obj(
          ("message", Json.fromString(e.msg)),
          ("cause", encodeCronError.apply(e.cause))
        )
      case a@AggregateError(_, _, _, _*) =>
        Json.obj(
          ("message", Json.fromString(a.msg)),
          ("cause", Json.arr(a.causes.map(encodeCronError.apply): _*))
        )
    }
  }
}
