package nextime.implicits

trait AllImplicits
  extends CronImplicits
    with AllPartImplicits
    with ValuePartImplicits
    with RangePartImplicits
    with IncrementPartImplicits
    with LastImplicits
