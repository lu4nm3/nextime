lazy val scala211Version      = "2.11.12"
lazy val scala212Version      = "2.12.6"

lazy val nextime = (project in file("."))
  .settings(
    scalacOptions ++= (
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n <= 11 => Seq(
          "-Ypartial-unification"              // Enable partial unification in type constructor inference
        )
        case _ => Seq(
          "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
          "-Ypartial-unification"              // Enable partial unification in type constructor inference
        )
      }
    ),
    scalaVersion := scala212Version,
    crossScalaVersions := Seq(scala211Version, scalaVersion.value)
  )
  .settings(
    name := "nextime",
    organization := "io.kleisli",
    description := "Nextime",
    homepage := Some(url("https://github.com/lu4nm3/nextime")),
    licenses += "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"),
    developers := List(
      Developer(
        id = "lu4nm3",
        name = "Luis Medina",
        email = "luis@kleisli.io",
        url = url("https://kleisli.io")
      )
    ),
    publishMavenStyle := true
  )
  .settings(
    credentials += Credentials(
      "Sonatype Nexus Repository Manager",
      "oss.sonatype.org",
      sys.env.getOrElse("SONATYPE_USERNAME", ""),
      sys.env.getOrElse("SONATYPE_PASSWORD", "")
    ),
    publishTo := sonatypePublishTo.value
  )
  .settings(
    useGpg := false,
    usePgpKeyHex("F48E42FA438D1910"),
    pgpPublicRing := baseDirectory.value / "travis" / "local.pubring.asc",
    pgpSecretRing := baseDirectory.value / "travis" / "local.secring.asc",
    pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toArray)
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