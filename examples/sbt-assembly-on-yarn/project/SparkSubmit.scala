import sbtsparksubmit.SparkSubmitPlugin.autoImport._

object SparkSubmit {
  lazy val settings: Seq[sbt.Def.Setting[_]] =
    SparkSubmitSetting("sparkPi",
        Seq(
          "--class", "SparkPi",
          "--num-executors", "1000"
        )
      )
}
