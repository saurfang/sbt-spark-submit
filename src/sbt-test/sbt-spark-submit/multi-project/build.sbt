lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "1.4.0" % "provided"
  )
)

lazy val root = (project in file("."))
  .aggregate(foo, bar)

lazy val foo = (project in file("foo"))
  .settings(commonSettings ++ SparkSubmit.settingsFoo: _*)

lazy val bar = (project in file("bar"))
  .dependsOn(foo)
  .settings(commonSettings ++ SparkSubmit.settingsBar: _*)
