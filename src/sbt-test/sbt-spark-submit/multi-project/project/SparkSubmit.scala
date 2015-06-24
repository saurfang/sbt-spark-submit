import sbtsparksubmit.SparkSubmitPlugin.autoImport._
import sbt._

object SparkSubmit {
  lazy val settingsFoo = SparkSubmitSetting(
    SparkSubmitSetting("sparkFoo").
      setting(sparkSubmitSparkArgs,
        "--class", "Main"
      )
  )
  lazy val settingsBar = SparkSubmitSetting(
    SparkSubmitSetting("sparkBar").
      setting(sparkSubmitSparkArgs,
        "--class", "Main"
      )
  )
}