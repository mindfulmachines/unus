import sbt._
import play.sbt.PlayImport._

lazy val commonSettings = Seq(
  organization := "io.mindfulmachines",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.8",
  description := " "
)

lazy val playDeps = Seq(
  libraryDependencies ++= Seq(
    jdbc,
    "org.flywaydb" %% "flyway-play" % "4.0.0",
    "org.apache.spark" %% "spark-core" % "2.1.1" exclude("org.apache.hadoop", "hadoop-client"),
    "org.apache.spark" %% "spark-sql" % "2.1.1",
    "org.apache.spark" %% "spark-mllib" % "2.1.1",
    "org.apache.hadoop" % "hadoop-client" % "2.7.3",
    "net.java.dev.jets3t" % "jets3t" % "0.9.4",
    "com.amazonaws" % "aws-java-sdk" % "1.11.157",
    "org.apache.hadoop" % "hadoop-aws" % "2.7.3"
  ),
  dependencyOverrides ++= Set("com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5")
)

routesGenerator := InjectedRoutesGenerator

lazy val sparkDeps = Seq(
  libraryDependencies ++= Seq(
    "org.postgresql" % "postgresql" % "42.1.1",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.apache.spark" %% "spark-core" % "2.1.1" % "provided" exclude("org.apache.hadoop", "hadoop-client"),
    "org.apache.spark" %% "spark-sql" % "2.1.1" % "provided",
    "org.apache.spark" %% "spark-mllib" % "2.1.1" % "provided",
    "org.apache.hadoop" % "hadoop-client" % "2.7.3" % "provided",
    "net.java.dev.jets3t" % "jets3t" % "0.9.4" % "provided",
    "com.amazonaws" % "aws-java-sdk" % "1.11.157" % "provided",
    "org.apache.hadoop" % "hadoop-aws" % "2.7.3" % "provided",
    "com.rockymadden.stringmetric" %% "stringmetric-core" % "0.27.4",
    "org.postgresql" % "postgresql" % "42.1.1",
    "io.getquill" %% "quill-jdbc" % "1.2.1"
  )
)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
resolvers += "Maven" at "http://repo1.maven.org/maven2"

crossScalaVersions := Seq("2.11.8")

pomIncludeRepository := { _ => false }

publishMavenStyle := true



fork in Test := true

javaOptions in Test ++= Seq("-Xmx1G")

lazy val root = (project in file(".")).
  enablePlugins(PlayScala).
  settings(commonSettings: _*).
  settings(playDeps: _*).
  settings(
    name := "unus"
  )
  .aggregate(library)
  .dependsOn(library)

lazy val library = (project in file("library")).
  settings(commonSettings: _*).
  settings(sparkDeps:_*).
  settings(
    name := "unus-library"
  )