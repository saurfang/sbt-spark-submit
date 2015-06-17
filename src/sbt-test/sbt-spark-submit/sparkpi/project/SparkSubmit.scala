import sbtsparksubmit.SparkSubmitPlugin.autoImport._

object SparkSubmit {
  lazy val settings =
    new SparkSetting("sparkPi") {
      override lazy val settings = Seq(
        sparkSubmitSparkArgs in sparkSubmit := Seq(
          "--class", "SparkPi"
        )
      )
    }
}