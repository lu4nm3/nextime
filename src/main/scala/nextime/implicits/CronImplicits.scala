package nextime
package implicits

trait CronImplicits {

  implicit class CronInterpolator(val sc: StringContext) {
    def cron(args: Any*): Either[Violation, Cron] = Cron(sc.raw())

    def ucron(args: Any*): Cron = Cron.unsafe(sc.raw())
  }

}
