import sbtsparksubmit.SparkSubmitPlugin.autoImport._

object SparkSubmit {
  lazy val settings =
    SparkSubmitSetting("sparkPi").
      setting(
        sparkSubmitSparkArgs,
        "--class", "SparkPi"
      )
}