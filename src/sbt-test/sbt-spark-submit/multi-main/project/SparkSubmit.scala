import sbtsparksubmit.SparkSubmitPlugin.autoImport._
import sbt._

object SparkSubmit {
  lazy val settings = SparkSubmitSetting(
    SparkSubmitSetting("spark1").
      setting(sparkSubmitSparkArgs,
        "--class", "Main1"
      ),
    SparkSubmitSetting("spark2").
      setting(sparkSubmitSparkArgs,
        "--class", "Main2"
      ),
    SparkSubmitSetting("spark2Other").
      setting(sparkSubmitSparkArgs,
        "--class", "Main2"
      ).
      setting(sparkSubmitAppArgs,
        "hello.txt"
      )
  )
}