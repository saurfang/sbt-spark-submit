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
    lazy val sparkSubmitJar = taskKey[String]("The job jar for spark-submit")
    lazy val sparkSubmitSparkArgs = settingKey[Seq[String]]("Arguments used for spark-submit")
    lazy val sparkSubmitAppArgs = settingKey[Seq[String]]("Arguments used for spark application")
    lazy val sparkSubmitMaster = settingKey[(Seq[String], Seq[String]) => String]("(SparkArgs, AppArgs) => Default Spark Master")
    lazy val sparkSubmitPropertiesFile = settingKey[Option[String]]("The default configuration file used by Spark")
    lazy val sparkSubmitClasspath = taskKey[Seq[File]]("Classpath used in SparkSubmit. For example, this can include the HADOOP_CONF_DIR for yarn deployment.")

    class SparkSubmitSetting(name: String) {
      lazy val sparkSubmit = InputKey[Unit](name,
        """Submit a Spark job. Usage: sbt "sparkSubmit --master=<local|yarn|...>
          |--class=<main class> [other options passed to spark-submit] --
          |[app arguments]".
          |Use -- to separate spark-submit arguments and application arguments.
        """.stripMargin)

      private[this] var settings: Seq[Setting[_]] = Seq()
      def toSettings = defaultSettings ++ settings
      def settings(settings: Setting[_]*): this.type = {
        this.settings ++= settings
        this
      }
      def setting[T](taskKey: TaskKey[T], value: T): this.type = {
        this.settings +:= (taskKey in this := value)
        this
      }
      def setting[T](settingKey: SettingKey[T], value: T): this.type = {
        this.settings +:= (settingKey in this := value)
        this
      }


      lazy val defaultSettings = Seq(
        sparkSubmit := {
          val jar = (sparkSubmitJar in sparkSubmit).value

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


          RunResult(runner.value.run(
            "org.apache.spark.deploy.SparkSubmit",
            sparkSubmitClasspath.value,
            options,
            streams.value.log)).foreach(sys.error)
        }
      )
    }

    object SparkSubmitSetting {
      def apply(name: String): SparkSubmitSetting = new SparkSubmitSetting(name)
      def apply(name: String, sparkArgs: Seq[String] = Seq(), appArgs: Seq[String] = Seq()): SparkSubmitSetting = {
        new SparkSubmitSetting(name).
          setting(sparkSubmitSparkArgs, sparkArgs).
          setting(sparkSubmitAppArgs, appArgs)
      }
      def apply(sparkSubmitSettings: SparkSubmitSetting*): Seq[Def.Setting[_]] = {
        sparkSubmitSettings.map(_.toSettings).reduce(_ ++ _)
      }
    }

    implicit def sparkSettingToSeq(s: SparkSubmitSetting): Seq[Def.Setting[_]] = s.toSettings
    implicit def sparkSettingToInputKey(s: SparkSubmitSetting): InputKey[Unit] = s.sparkSubmit
  }

  import autoImport._

  /**
   * Provide default settings
   */
  override def projectSettings: Seq[Def.Setting[_]] = defaultSparkSubmitSetting ++
    Seq(
      sparkSubmitJar := (packageBin in Compile).value.getAbsolutePath,
      sparkSubmitAppArgs := Seq(),
      sparkSubmitSparkArgs := Seq(),
      sparkSubmitMaster := {(_, _) => "local"},
      sparkSubmitPropertiesFile := None,
      sparkSubmitClasspath := data((fullClasspath in Compile).value)
    )

  def defaultSparkSubmitSetting: SparkSubmitSetting = SparkSubmitSetting("sparkSubmit")

  override def trigger = allRequirements
}

object SparkSubmitYARN extends AutoPlugin {
  override def requires = SparkSubmitPlugin

  import SparkSubmitPlugin.autoImport._
  override def projectSettings = Seq(
    //defaults to yarn-cluster if approriate
    sparkSubmitMaster := {
      (sparkArgs, appArgs) =>
        if(appArgs.contains("--help"))
          "local"
        else
          "yarn-cluster"
    },
    //include HADOOP/YARN CONF in classpath
    sparkSubmitClasspath := {
      Seq("HADOOP_CONF_DIR", "YARN_CONF_DIR").flatMap(sys.env.get).map(new File(_)) ++
        data((fullClasspath in Compile).value)
    }
  )
}
