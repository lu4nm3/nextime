package nextime
package time
package intervals

import scala.collection.immutable.SortedSet

trait BasePartIntervals {
  def baseIntervals(implicit bounds: Bounds,
                    valueInter: Interval[Value],
                    rangeInter: Interval[Range],
                    allInter: Interval[All.type],
                    incrementInter: Interval[Increment]): PartialFunction[PartExpression, SortedSet[Int]] = {
    case value@Value(_) => valueInter.interval(value)
    case range@Range(_, _) => rangeInter.interval(range)
    case all@All => allInter.interval(all)
    case increment@Increment(_, _) => incrementInter.interval(increment)
  }

  implicit def valueInterval(implicit bounds: Bounds): Interval[Value] = new Interval[Value] {
    override def interval(value: Value): SortedSet[Int] = {
      SortedSet(value.value)
    }
  }

  implicit def rangeInterval(implicit bounds: Bounds): Interval[Range] = new Interval[Range] {
    override def interval(range: Range): SortedSet[Int] = {
      if (range.lower.value > range.upper.value) {
        SortedSet(range.lower.value to bounds.upper: _*) ++ SortedSet(bounds.lower to range.upper.value: _*)
      } else {
        SortedSet(range.lower.value to range.upper.value: _*)
      }
    }
  }

  implicit def allInterval(implicit bounds: Bounds): Interval[All.type] = new Interval[All.type] {
    def interval(all: All.type): SortedSet[Int] = {
      SortedSet(bounds.lower to bounds.upper: _*)
    }
  }

  implicit def incrementInterval(implicit bounds: Bounds): Interval[Increment] = new Interval[Increment] {
    override def interval(increment: Increment): SortedSet[Int] = increment match {
      case Increment(Some(bound), inc) => bound match {
        case Value(value) => SortedSet(value to bounds.upper by inc.value: _*)
        case Range(lower, upper) =>
          if (lower.value > upper.value) {
            SortedSet(lower.value to bounds.upper by inc.value: _*) ++
              SortedSet(bounds.lower to upper.value by inc.value: _*)
          } else {
            SortedSet(lower.value to upper.value by inc.value: _*)
          }
        case _: All.type => SortedSet(bounds.lower to bounds.upper by inc.value: _*)
      }
      case Increment(None, inc) => SortedSet(bounds.lower to bounds.upper by inc.value: _*)
    }
  }
}
