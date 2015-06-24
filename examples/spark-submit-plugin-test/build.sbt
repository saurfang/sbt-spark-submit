lazy val root = (project in file("."))
  .settings(SparkSubmit.settings: _*)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.4.0" % "provided"
)
