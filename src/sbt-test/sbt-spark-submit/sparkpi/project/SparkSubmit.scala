import sbtsparksubmit.SparkSubmitPlugin.autoImport._

object SparkSubmit {
  lazy val settings = SparkSubmitSetting("sparkPi", Seq("--class", "SparkPi"))
}
