import sbt.Setting
import sbtsparksubmit.SparkSubmitPlugin.autoImport._

object SparkSubmit {
  lazy val settings: Seq[Setting[_]] =
    new SparkSetting("sparkPi") {
      override lazy val settings = Seq(
        sparkSubmitSparkArgs in sparkSubmit := Seq(
          "--class", "SparkPi",
          "--num-executors", "1000"
        )
      )
    }
}
