lazy val nextime = (project in file("."))
  .settings(
    scalacOptions := Seq("-feature", "-Ypartial-unification"),
    scalaVersion := "2.12.1"//,
//    version := "0.0.1"
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
//    scmInfo := Some(ScmInfo(
//      url("https://github.com/lu4nm3/nextime"),
//      "scm:git:https://github.com/lu4nm3/nextime.git",
//      Some(s"scm:git:git@github.com:lu4nm3/nextime.git")
//    )),
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
//    isSnapshot := version.value endsWith "SNAPSHOT",
//    publishTo := Some(
//      if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
//      else Opts.resolver.sonatypeStaging
//    )
  )
  .settings(
//    credentials += Credentials(
//      "PGP Secret Key",
//      "pgp",
//      "sbt",
//      sys.env.getOrElse("PGP_PASSPHRASE", "")
//    ),
    useGpg := false,
    usePgpKeyHex("B796B8AFE484EC92"),
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