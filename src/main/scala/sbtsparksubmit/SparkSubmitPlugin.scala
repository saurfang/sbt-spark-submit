package sbtsparksubmit

import sbt._
import Keys._
import sbt.complete.DefaultParsers._
import Attributed._
import scala.language.implicitConversions

/**
 * This plugin helps you submit Spark job from sbt
 */
object SparkSubmitPlugin extends AutoPlugin {

  /**
   * Defines all settings/tasks that get automatically imported,
   * when the plugin is enabled
   */
  object autoImport {
    lazy val sparkSubmitJar = taskKey[File]("The job jar for spark-submit")
    lazy val sparkSubmitSparkArgs = settingKey[Seq[String]]("Arguments used for spark-submit")
    lazy val sparkSubmitAppArgs = settingKey[Seq[String]]("Arguments used for spark application")
    lazy val sparkSubmitMaster = settingKey[(Seq[String], Seq[String]) => String]("(SparkArgs, AppArgs) => Default Spark Master")
    lazy val sparkSubmitPropertiesFile = settingKey[Option[String]]("The default configuration file used by Spark")

    class SparkSetting(name: String) {
      lazy val sparkSubmit = InputKey[Unit](name,
        """Submit a Spark job. Usage: sbt "sparkSubmit --master=<local|yarn|...>
          |--class=<main class> [other options passed to spark-submit] --
          |[app arguments]".
          |Use -- to separate spark-submit arguments and application arguments.
        """.stripMargin)

      lazy val settings: Seq[Setting[_]] = Seq()

      lazy val defaultSettings = Seq(
        sparkSubmit := {
          val jar = (sparkSubmitJar in sparkSubmit).value.getAbsolutePath

          var sparkArgs = (sparkSubmitSparkArgs in sparkSubmit).value
          var appArgs = (sparkSubmitAppArgs in sparkSubmit).value

          val args = spaceDelimited("<arg>").parsed
          if (args.nonEmpty) {
            val (first, second) = args.splitAt(args.indexOf("--"))
            sparkArgs ++= first
            appArgs ++= second.filterNot(_ == "--")
          }

          val options: Seq[String] = {
            if (sparkArgs.contains("--help") || (sparkArgs.isEmpty && appArgs.isEmpty)) {
              Seq("--help")
            } else {
              if (!sparkArgs.contains("--master")) {
                sparkArgs ++= Seq("--master", sparkSubmitMaster.value(sparkArgs, appArgs))
              }

              sparkSubmitPropertiesFile.value foreach {
                file =>
                  if (!sparkArgs.contains("--properties-file")) {
                    sparkArgs ++= Seq("--properties-file", file)
                  }
              }

              (sparkArgs :+ jar) ++ appArgs
            }
          }

          runner.value.run(
            "org.apache.spark.deploy.SparkSubmit",
            data((fullClasspath in Compile).value),
            options,
            streams.value.log) foreach println

        }
      )
    }

    implicit def sparkSettingsToSeq(s: SparkSetting): Seq[Def.Setting[_]] = {
      s.defaultSettings ++ s.settings
    }
  }

  import autoImport._

  /**
   * Provide default settings
   */
  override def projectSettings: Seq[Def.Setting[_]] = defaultSparkSubmitSetting ++
    Seq(
      sparkSubmitJar := (packageBin in Compile).value,
      sparkSubmitAppArgs := Seq(),
      sparkSubmitSparkArgs := Seq(),
      sparkSubmitMaster := {(_, _) => "local"},
      sparkSubmitPropertiesFile := None
    )

  def defaultSparkSubmitSetting: SparkSetting = new SparkSetting("sparkSubmit") {

  }

  override def trigger = allRequirements
}