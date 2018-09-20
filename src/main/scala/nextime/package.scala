import nextime.constants.AllConstants
import nextime.implicits.AllImplicits
import nextime.validation.Violation

package object nextime extends Parts with AllImplicits with AllConstants {
  type Maybe[A] = Either[Violation, A]
}
