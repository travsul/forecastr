ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

val http4sVersion = "1.0.0-M41"
val log4catsVersion = "2.7.0"
val circeVersion = "0.14.1"
val catsEffectTestVersion = "1.5.0"
val scalaTestVersion = "3.2.19"

lazy val root = (project in file("."))
  .settings(
    name := "forecastr",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.typelevel" %% "log4cats-slf4j"   % log4catsVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "org.typelevel" %% "cats-effect-testing-scalatest" % catsEffectTestVersion % Test
    )
  )
