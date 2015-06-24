import sbtsparksubmit.SparkSubmitPlugin.autoImport._
import sbt._

object SparkSubmit {
  lazy val settingsFoo = SparkSubmitSetting(
    SparkSubmitSetting("sparkFoo", Seq("--class", "Main"))
  )
  lazy val settingsBar = SparkSubmitSetting(
    SparkSubmitSetting("sparkBar", Seq("--class", "Main"))
  )
}
