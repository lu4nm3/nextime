# Nextime [![License][licenseImg]][licenseLink] [![TravisCI][travisCiImg]][travisCiLink]

Nextime is a library for Scala that helps you figure out the "next time" something should occur by providing an easy and type safe way to work with cron expressions.

[//]: <> (Links)

[travisCiImg]: https://img.shields.io/travis/lu4nm3/nextime/master.svg
[travisCiLink]: https://travis-ci.org/lu4nm3/nextime

[licenseImg]: https://img.shields.io/github/license/lu4nm3/nextime.svg
[licenseLink]: LICENSE

[mavenImg]: https://img.shields.io/maven-central/v/io.kleisli/nextime_2.12.svg
[mavenLink]: https://search.maven.org/search?q=nextime

# Guide

  1. [Installation](#installation)
  2. [Expressions](#expressions)
  3. [Time](#time)
  4. [Violations](#violations)
  
# Installation

Latest version: [![Maven][mavenImg]][mavenLink]

In your `build.sbt`, add the following:

```scala
libraryDependencies += "io.kleisli" %% "nextime" % "version"
```

And then simply import nextime into your project:

```scala
import nextime._
```

# Quick Start

```scala
import nextime._

val minute = Minute(3)
val hour = Hour(5)
val dayOfMonth = DayOfMonth(11)
val month = Month(4)
val dayOfWeek = DayOfWeek(?)

Cron(minute, hour, dayOfMonth, month, dayOfWeek) // Maybe[Cron]
```

# Expressions

You can think of cron expressions as being made up of several multipart expressions (ie. second, minute, hour, etc) which in turn are made up of 1 or more part expressions.

## Cron Expressions

The `Cron` type uses smart constructors to create instances of `Maybe[Cron]` which represents the possibility of errors in the cron expression. Note that `Maybe[Cron]` is just a type alias for `Either[Violation, Cron]`:

```scala
val cron: Either[Violation, Cron] = Cron(minute, hour, dayOfMonth, month, dayOfWeek)
```

Only 3 permutations of multipart expressions are supported in a cron expression:

| Permutation | Second | Minute | Hour | Day of Month | Month | Day of Week | Year |
| ----------- | ------ | ------ | ---- | ------------ | ----- | ----------- | ---- |
| #1          |        | ✓      | ✓    | ✓            | ✓     | ✓           |      |
| #2          | ✓      | ✓      | ✓    | ✓            | ✓     | ✓           |      |
| #3          | ✓      | ✓      | ✓    | ✓            | ✓     | ✓           | ✓    |

You can also create a `Cron` expression directly from a string representation through the constructor:

```scala
val cron = Cron("3 5 11 4 ?")
```

As well as through the `cron` string interpolator:

```scala
val cron = cron"3 5 11 4 ?"
```

And you can get back the string representation of a cron expression using `mkString`:

```scala
val s: Maybe[String] = cron.map(_.mkString) // Right("3 5 11 4 ?")
```

## Multipart Expressions

A multipart expression is one of `Second`, `Minute`, `Hour`, `DayOfMonth`, `Month`, `DayOfWeek`, and `Year`. When you use them together in one of the predefined permutations, you can create a `Cron` expression:

```scala
Cron(Minute(...), Hour(...), DayOfMonth(...), Month(...), DayOfWeek(...))
```

Multipart expressions are made up of at least 1 or more part expressions, hence why they are "multipart". And just like with `Cron` expressions, multipart expressions use smart constructors to encapsulate the possibility of having errors:

```scala
val second = Second(*) // Maybe[Second]

val minute = Minute(3, 21, 56) // Maybe[Minute]

val hour = Hour(17) // Maybe[Hour]
```

The parts that you can use in a multipart expression vary. Every multipart expression has a lower and upper bounds on the values that you're allowed to use and not all of them support the same part expressions:

| Multipart Expression | Allowed Values      | Allowed Parts (where X is a value)  |
| -------------------- | ------------------- | ----------------------------------- |
| Second               | `0-59`              | `*` `-` `/`                         |
| Minute               | `0-59`              | `*` `-` `/`                         |
| Hour                 | `0-23`              | `*` `-` `/`                         |
| Day of Month         | `1-31`              | `*` `-` `/` `?` `L` `L-X` `XW` `LW` |
| Month                | `1-12` or `JAN-DEC` | `*` `-` `/`                         |
| Day of Week          | `1-7` or `SUN-SAT`  | `*` `-` `/` `?` `L` `XL` `X#X`      |
| Year                 | `1970-2099`         | `*` `-` `/`                         |

## Part Expressions

Part expressions are the lowest level building block for cron expressions. Nextime supports all of the same expressions as the Java [Quartz][quartz] library through a series of custom types. It also provides shorthand versions of these types for convenience.

[//]: <> (Links)

[quartz]: http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html

| Symbol      | Type                                  | Shorthand             | English                                                                                                                                  |
| ----------- | ------------------------------------- | --------------------- |----------------------------------------------------------------------------------------------------------------------------------------- |
| *           | `All`                                 | `*`                   | All possible values for the given field.                                                                                                 |
| 3           | `Value(3)`                            | `3`                   | The value 3.                                                                                                                             |
| 1-3         | `Range(1, 3)`                         | `1 ~- 3`              | The values 1, 2, and 3.                                                                                                                  |
| 1/3 <br> /3 | `Increment(1, 3)` <br> `Increment(3)` | `1 ~/ 3` <br> `~/(3)` | The values 1, 4, 7, ... up to the upper bound of the given field. <br> The values 0, 3, 6, ... up to the upper bound of the given field. |
| ?           | `NoValue`                             | `?`                   | Ignore the day-of-month OR the day-of-week but NOT both.                                                                                 |
| L           | `Last`                                | `L`                   | The last day of the month OR the last day of the week (Saturday).                                                                        |
| 3L          | `LastDayOfMonth(3)`                   | `3.L`                 | The last Tuesday of the month.                                                                                                           |
| L-3         | `LastOffset(3)`                       | `L-3`                 | The third to last day of the month.                                                                                                      |
| 3W          | `Weekday(3)`                          | `3.W`                 | The nearest weekday to the 3rd of the month.                                                                                             |
| LW          | `LastWeekday`                         | `LW`                  | The last weekday of the month.                                                                                                           |
| 1#3         | `NthXDayOfMonth(1, 3)`                | `1 ~# 3`              | The third Sunday (1 = Sunday) of the month.                                                                                              |

# Time

Nextime supports various ways of using cron expressions to work with time.

## Next time

Given a `Cron` expression, use the `next` method to find the next time it will be triggered:

```scala
import org.joda.time.DateTime
import nextime._

val cron = Cron("0 0 3 11 4 ? *")
val dateTime = new DateTime(2018, 4, 21, 3, 0, DateTimeZone.forID("America/Los_Angeles"))

cron.map(_.next(dateTime)) // Right(Some(2019-04-11T03:00:00.000-07:00))
```

You'll notice that the return type was an `Option[DateTime]` which represents the possibility that there are no more future date times that match the cron expression:

```scala
import org.joda.time.DateTime
import nextime._

val cron = Cron("0 0 3 11 4 ? 2018")
val dateTime = new DateTime(2018, 4, 21, 3, 0, DateTimeZone.forID("America/Los_Angeles"))

cron.map(_.next(dateTime)) // Right(None)
```

Note that currently Nextime only `DateTime` values from the Joda Time library are supported. Future enhancements will extend this to support other date time types.

## Previous time

Nextime also supports the ability to find the previous time that a cron expression was triggered through the `previous` method:

```scala
import org.joda.time.DateTime
import nextime._

val cron = Cron("0 0 3 11 4 ? *")
val dateTime = new DateTime(2018, 4, 21, 3, 0, DateTimeZone.forID("America/Los_Angeles"))

cron.map(_.previous(dateTime)) // Right(Some(2018-04-11T03:00:00.000-07:00))
```

Just like before, the return type is an `Option[DateTime]` for the cases when a cron expression can not be triggered in the past:

```scala
import org.joda.time.DateTime
import nextime._

val cron = Cron("0 0 3 11 4 ? 2018")
val dateTime = new DateTime(2017, 4, 21, 3, 0, DateTimeZone.forID("America/Los_Angeles"))

cron.map(_.previous(dateTime)) // Right(None)
```

# Violations



