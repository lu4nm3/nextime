# Nextime [![License][licenseImg]][licenseLink] [![TravisCI][travisCiImg]][travisCiLink]

Nextime is a library for Scala that helps you figure out the "next time" something should occur by providing an easy and 
type safe way to work with cron expressions.

[licenseImg]: https://img.shields.io/github/license/lu4nm3/nextime.svg
[licenseLink]: LICENSE

[travisCiImg]: https://img.shields.io/travis/lu4nm3/nextime/master.svg
[travisCiLink]: https://travis-ci.org/lu4nm3/nextime


# Guide

  1. [Installation](#installation)
  2. [Expressions](#expressions)
  3. [Time](#time)
  4. [Errors](#errors)

# Installation

Latest version: [![Maven][mavenImg]][mavenLink]

[mavenImg]: https://img.shields.io/maven-central/v/io.kleisli/nextime_2.12.svg
[mavenLink]: https://search.maven.org/search?q=nextime

In your `build.sbt` file, add the following:

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

val minute = Minute(0)
val hour = Hour(3)
val dayOfMonth = DayOfMonth(11)
val month = Month(4)
val dayOfWeek = DayOfWeek(?)

Cron(minute, hour, dayOfMonth, month, dayOfWeek) // Either[nextime.Error, Cron]
```

# Expressions

You can think of cron expressions as being made up of several multipart expressions (ie. second, minute, hour, etc) 
which in turn are made up of 1 or more part expressions.

## Cron Expressions

The `Cron` type uses smart constructors to create instances of `Either[nextime.Error, Cron]` to account for the 
possibility of errors in the cron expression:

```scala
val cron: Either[nextime.Error, Cron] = Cron(minute, hour, dayOfMonth, month, dayOfWeek)
```

Only 3 permutations of multipart expressions are supported in a cron expression:

| Permutation | Second | Minute | Hour | Day of Month | Month | Day of Week | Year |
| ----------- | ------ | ------ | ---- | ------------ | ----- | ----------- | ---- |
| #1          |        | ✓      | ✓    | ✓            | ✓     | ✓           |      |
| #2          | ✓      | ✓      | ✓    | ✓            | ✓     | ✓           |      |
| #3          | ✓      | ✓      | ✓    | ✓            | ✓     | ✓           | ✓    |

You can also build a `Cron` expression directly from a string representation using a constructor:

```scala
val cron: Either[nextime.Error, Cron] = Cron("0 3 11 4 ?")
```

As well as the `cron` string interpolator:

```scala
val cron: Either[nextime.Error, Cron] = cron"0 3 11 4 ?"
```

And you can get back the string representation of a cron expression using `mkString`:

```scala
cron.map(_.mkString) // Right("0 3 11 4 ?")
```

If you're feeling adventurous, you can use the `unsafe` version of these constructors which will return a `Cron` value 
directly:

```scala
val cron1: Cron = Cron.unsafe(minute, hour, dayOfMonth, month, dayOfWeek)

val cron2: Cron = Cron.unsafe("0 3 11 4 ?")

val cron3: Cron = ucron"0 3 11 4 ?"
```

Just be careful as the `unsafe` constructors will throw exceptions when a cron expression is invalid:

```scala
scala> Cron.unsafe("0 3 -11 4 ?")
nextime.Error$UniqueError:
{
    "message" : "Invalid cron expression",
    "cause" : {
        "message" : "Invalid day of month expression",
        "cause" : {
            "message" : "Numeric values must be between 1 and 31",
            "cause" : "-11 is out of bounds"
        }
    }
}
  at nextime.Error$.apply(Error.scala:53)
  at nextime.Error$.apply(Error.scala:45)
  ...
```

Read more about cron errors in the section [below](#errors).

## Multipart Expressions

A multipart expression is one of `Second`, `Minute`, `Hour`, `DayOfMonth`, `Month`, `DayOfWeek`, and `Year`. When you 
use them together in one of the predefined permutations, you can create a `Cron` expression:

```scala
Cron(Minute(...), Hour(...), DayOfMonth(...), Month(...), DayOfWeek(...))
```

Multipart expressions are made up of at least 1 or more part expressions, hence why they are "multipart". And just like 
with `Cron` expressions, multipart expressions use smart constructors to encapsulate the possibility of having errors:

```scala
val second: Either[nextime.Error, Second] = Second(*)

val minute: Either[nextime.Error, Minute] = Minute(3, 21, 56)

val hour: Either[nextime.Error, Hour] = Hour(17)
```

In addition, you can build multipart expressions from their string representation using a constructor:
 
```scala
val second: Either[nextime.Error, Second] = Second("*")

val minute: Either[nextime.Error, Minute] = Minute("3,21,56")

val hour: Either[nextime.Error, Hour] = Hour("17")
```

As well as their respective string interpolators (`sec`, `min`, `hr`, `dom`, `mon`, `dow`, `yr`):

```scala
val second: Either[nextime.Error, Second] = sec"*"

val minute: Either[nextime.Error, Minute] = min"3,21,56"

val hour: Either[nextime.Error, Hour] = hr"17"
```
 
The parts that you can use in a multipart expression vary. Every multipart expression has a lower and upper bounds on 
the values that you're allowed to use and not all of them support the same [part expressions](#part-expressions):

| Multipart Expression | Allowed Values      | Allowed Parts (where X is a numeric value) |
| -------------------- | ------------------- | ------------------------------------------ |
| Second               | `0-59`              | `*` `-` `/`                                |
| Minute               | `0-59`              | `*` `-` `/`                                |
| Hour                 | `0-23`              | `*` `-` `/`                                |
| Day of Month         | `1-31`              | `*` `-` `/` `?` `L` `L-X` `XW` `LW`        |
| Month                | `1-12` or `JAN-DEC` | `*` `-` `/`                                |
| Day of Week          | `1-7` or `SUN-SAT`  | `*` `-` `/` `?` `L` `XL` `X#X`             |
| Year                 | `1970-2099`         | `*` `-` `/`                                |

Much like `Cron`, multipart expressions provide `unsafe` constructors to build instances directly:

```scala
val second: Second = Second.unsafe(*)

val minute: Minute = Minute.unsafe("3,21,56")

val hour: Hour = Hour.unsafe(17)

val dayOfMonth: DayOfMonth = udom"11"
```

And if used on an invalid multipart expression, an exception will be thrown:

```scala
scala> Hour.unsafe(-3)
nextime.Error$UniqueError:
{
    "message" : "Invalid hour expression",
    "cause" : {
        "message" : "Numeric values must be between 0 and 23",
        "cause" : "-3 is out of bounds"
    }
}
  at nextime.Error$.apply(Error.scala:53)
  at nextime.Error$.apply(Error.scala:45)
  ...
```

## Part Expressions

Part expressions are the lowest level building block for cron expressions. Nextime supports all of the same expressions 
as the Java [Quartz][quartz] library through a series of custom types. It also provides shorthand versions of these 
types for convenience.

[quartz]: http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html

| Symbol      | Type                                  | Shorthand             | English                                                                                                                                 |
| ----------- | ------------------------------------- | --------------------- |---------------------------------------------------------------------------------------------------------------------------------------- |
| *           | `All`                                 | `*`                   | All possible values for the given field                                                                                                 |
| 3           | `Value(3)`                            | `3`                   | The value 3                                                                                                                             |
| 1-3         | `Range(1, 3)`                         | `1 ~- 3`              | The values 1, 2, and 3                                                                                                                  |
| 1/3 <br> /3 | `Increment(1, 3)` <br> `Increment(3)` | `1 ~/ 3` <br> `~/(3)` | The values 1, 4, 7, ... up to the upper bound of the given field. <br> The values 0, 3, 6, ... up to the upper bound of the given field |
| ?           | `NoValue`                             | `?`                   | Ignore the day-of-month OR the day-of-week but NOT both                                                                                 |
| L           | `Last`                                | `L`                   | The last day of the month OR the last day of the week (Saturday)                                                                        |
| 3L          | `LastDayOfMonth(3)`                   | `3.L`                 | The last Tuesday of the month                                                                                                           |
| L-3         | `LastOffset(3)`                       | `L-3`                 | The third to last day of the month                                                                                                      |
| 3W          | `Weekday(3)`                          | `3.W`                 | The nearest weekday to the 3rd of the month                                                                                             |
| LW          | `LastWeekday`                         | `LW`                  | The last weekday of the month                                                                                                           |
| 1#3         | `NthXDayOfMonth(1, 3)`                | `1 ~# 3`              | The third Sunday (1 = Sunday) of the month                                                                                              |

# Time

Nextime supports a couple of ways to work with time using cron expressions.

Given a `Cron` expression and a particular point in time:

```scala
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import nextime._

val cron = Cron("0 0 3 11 4 ? *")
val dateTime = new DateTime(2018, 4, 11, 3, 0, DateTimeZone.forID("America/Los_Angeles"))
```

You can use the `next` method to find the next time it will be triggered:

```scala
cron.map(_.next(dateTime)) // Right(Some(2019-04-11T03:00:00.000-07:00))
```

Similarly, you can use the `previous` method to find the last time that a cron expression was triggered:

```scala
cron.map(_.previous(dateTime)) // Right(Some(2017-04-11T03:00:00.000-07:00))
```

You'll notice that the return type for both was an `Option[DateTime]` which represents the possibility that there are no 
past or future date times that match the cron expression:

```scala
val cron = Cron("0 0 3 11 4 ? 2018")

cron.map(_.next(dateTime)) // Right(None)

cron.map(_.previous(dateTime)) // Right(None)
```

Note that currently Nextime only supports `DateTime` values from the Joda Time library. Future enhancements will extend 
this to support other date types.

# Errors

Most of the time you're able to use types to effectively figure out which part expressions are supported by a particular 
multipart expression. For example, only `DayOfMonth` supports the `LastWeekday` part expression and using `LastWeekday` 
for any other multipart expression will result in a compilation error. 

For the times when this isn't enough, Nextime provides an intricate error system that is used to describe issues that 
your cron expression and its sub-expressions may have.

You can think of `nextime.Error` as a recursive structure consisting of a main message describing the error along with 1 
or more causes of the error. Nextime uses the Circe library to print the errors in JSON format for easier inspection.

### Simple errors

As an example, let's take a look at an error generated from an invalid `Minute` expression:

```scala
scala> Minute(-3)
res0: Either[nextime.Error,nextime.Minute] =
Left(nextime.Error$UniqueError:
{
    "message" : "Invalid minute expression",
    "cause" : {
        "message" : "Numeric values must be between 0 and 59",
        "cause" : "-3 is out of bounds"
    }
})
```

Here we see that the top-level error is that we tried to create an invalid minute expression. If you look the cause of,
the error, you will notice that it was due to an invalid numeric value (`-3`) which is out of bounds for the range of 
values supported by the minute expression (`0-59`).

### Aggregate errors

It's possible to have several errors in our expression. When this happens, all of the causes for the same type of error 
are aggregated together.

Here we have a `Minute` expression containing 2 numeric values and 1 range value:

```scala
scala> Minute(-3, -46, 5 ~- 78)
res0: Either[nextime.Error,nextime.Minute] =
Left(nextime.Error$AggregateError:
{
    "message" : "Invalid minute expression",
    "cause" : [
        {
            "message" : "Numeric values must be between 0 and 59",
            "cause" : [
                "-46 is out of bounds",
                "-3 is out of bounds"
            ]
        },
        {
            "message" : "Range lower and upper values must be between 0 and 59",
            "cause" : "78 is out of bounds"
        }
    ]
})
```

Both numeric values are negative which makes them invalid. Since both are the same type of error, in this case an
invalid numeric expression, their causes are aggregated together into a list. The range expression is also invalid
because it's out of bounds. However, since this error is different from the first one (ie. it's an error having to do 
with an invalid range expression), it is treated as a separate type of error entirely and is thus not aggregated with 
the rest.

### Grouped errors

When building a `Cron` expression, it's possible for 1 or more of the multipart expressions that make up the cron
expression to have errors in them. If this happens, Nextime returns all of the multipart errors together:
 
```scala
scala> Cron(Minute(-3, -46, 5 ~- 78), Hour(1, 2, 3), DayOfMonth(-11), Month(0, 4, 15), DayOfWeek(?))
res0: Either[nextime.Error,nextime.Cron] =
Left(nextime.Error$AggregateError: 
{
    "message" : "Invalid cron expression",
    "cause" : [
        {
            "message" : "Invalid day of month expression",
            "cause" : {
                "message" : "Numeric values must be between 1 and 31",
                "cause" : "-11 is out of bounds"
            }
        },
        {
            "message" : "Invalid month expression",
            "cause" : {
                "message" : "Numeric values must be between 1 and 12",
                "cause" : [
                    "0 is out of bounds",
                    "15 is out of bounds"
                ]
            }
        },
        {
            "message" : "Invalid minute expression",
            "cause" : [
                {
                    "message" : "Numeric values must be between 0 and 59",
                    "cause" : [
                        "-46 is out of bounds",
                        "-3 is out of bounds"
                    ]
                },
                {
                    "message" : "Range lower and upper values must be between 0 and 59",
                    "cause" : "78 is out of bounds"
                }
            ]
        }
    ]
})
```

Nextime does this by using a `Semigroupal` to group all of the errors together which makes it much more convenient than 
having to constantly fix and recompile each individual error until there are no more.