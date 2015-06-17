sbtPlugin := true

//Change to your organization
organization := "com.github.saurfang"

//Change to your plugin name
name := """sbt-spark-submit"""

//Change to the version
version := "0.1-SNAPSHOT"

scalaVersion := "2.10.5"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += Resolver.sonatypeRepo("snapshots")

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"


// Scripted - sbt plugin tests
scriptedSettings

scriptedLaunchOpts += "-Dplugin.version=" + version.value
