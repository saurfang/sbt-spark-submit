import sbtsparksubmit.SparkSubmitPlugin.autoImport._
import sbt._

object SparkSubmit {
  lazy val settings = SparkSubmitSetting(
    SparkSubmitSetting("spark1",
      Seq("--class", "Main1")
    ),
    SparkSubmitSetting("spark2",
      Seq("--class", "Main2")
    ),
    SparkSubmitSetting("spark2Other",
      Seq("--class", "Main2"),
      Seq("hello.txt")
    )
  )
}
