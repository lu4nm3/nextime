lazy val nextime = (project in file("."))
  .settings(
    name := "nextime",
    scalacOptions := Seq("-feature", "-Ypartial-unification"),
    scalaVersion := "2.12.1",
    version := "0.0.1"
  )
  .settings(libraryDependencies ++= Seq(
    "com.chuusai" %% "shapeless" % "2.3.2" withSources(),
    "io.circe" %% "circe-core" % "0.9.2",
    "io.circe" %% "circe-generic" % "0.9.2",
    "com.lihaoyi" %% "fastparse" % "1.0.0" withSources() withJavadoc(),
    "joda-time" % "joda-time" % "2.9.9" withSources() withJavadoc(),
    "org.typelevel" %% "cats-core" % "1.1.0" withSources() withJavadoc(),
    "org.scalatest" %% "scalatest" % "3.0.4" % "test" withSources() withJavadoc()
  ))