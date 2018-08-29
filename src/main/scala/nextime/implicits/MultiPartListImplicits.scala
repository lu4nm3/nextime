package nextime
package implicits

import scala.language.implicitConversions

trait MultiPartListImplicits {
  implicit def secondPartList[A](l: List[A])(implicit f: A => SecondPart): List[SecondPart] = l.map(f)

  implicit def minutePartList[A](l: List[A])(implicit f: A => MinutePart): List[MinutePart] = l.map(f)

  implicit def hourPartList[A](l: List[A])(implicit f: A => HourPart): List[HourPart] = l.map(f)

  implicit def dayOfMonthPartList[A](l: List[A])(implicit f: A => DayOfMonthPart): List[DayOfMonthPart] = l.map(f)

  implicit def monthPartList[A](l: List[A])(implicit f: A => MonthPart): List[MonthPart] = l.map(f)

  implicit def dayOfWeekPartList[A](l: List[A])(implicit f: A => DayOfWeekPart): List[DayOfWeekPart] = l.map(f)

  implicit def yearPartList[A](l: List[A])(implicit f: A => YearPart): List[YearPart] = l.map(f)
}
