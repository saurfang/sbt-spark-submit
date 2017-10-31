lazy val commonSettings = Seq(
  scalaVersion := "2.10.6",
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "1.4.0" % "provided"
  )
)

lazy val root = (project in file("."))
  .disablePlugins(SparkSubmitPlugin)
  .aggregate(foo, bar)

lazy val foo = (project in file("foo"))
  .settings(commonSettings ++ SparkSubmit.settingsFoo: _*)

lazy val bar = (project in file("bar"))
  .dependsOn(foo)
  .settings(commonSettings ++ SparkSubmit.settingsBar: _*)
