package nextime
package implicits

trait CronImplicits {

  implicit class CronInterpolator(val sc: StringContext) {
    def cron(args: Any*): Maybe[Cron] = Cron(sc.raw())
  }

}
