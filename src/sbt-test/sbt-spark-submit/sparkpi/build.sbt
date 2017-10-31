name := "SparkPi"
scalaVersion := "2.10.6"

lazy val root = (project in file("."))
  .settings(SparkSubmit.settings: _*)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.4.0" % "provided"
)
