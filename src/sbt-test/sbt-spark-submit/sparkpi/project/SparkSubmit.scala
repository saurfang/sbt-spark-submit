import sbtsparksubmit.SparkSubmitPlugin.autoImport._

object SparkSubmit {
  lazy val settings = SparkSubmitSetting("sparkPi", Seq("--class", "SparkPi")) ++ {
    val task = SparkSubmitSetting("sparkPi2", Seq(), Seq("5"))
    task.settings(sparkSubmitSparkArgs in task := {
      Seq("--class", sbt.Keys.name.value) //demostrate we can wire this with other settingKeys
    })
  }
}
