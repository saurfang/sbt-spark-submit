import sbtsparksubmit.SparkSubmitPlugin.autoImport._
import sbtassembly.AssemblyKeys._
import awscala._
import ec2._
import sbt._

object SparkSubmit {
  lazy val settings =
    SparkSubmitSetting(
      SparkSubmitSetting("sparkPi",
        Seq(
          "--class", "SparkPi"
        )
      ),
      {
        val task = SparkSubmitSetting("sparkPi-ec2")
        import EC2Config._
        task.settings(sparkSubmitSparkArgs in task := {
          Seq(
            "--master", getMaster.map(i => s"spark://${i.publicDnsName}:6066").getOrElse(""),
            "--deploy-mode", "cluster",
            "--class", "SparkPi"
          )
        },
        sparkSubmitJar in task := {
          val file = assembly.value
          val filename = file.getName
          getMaster.foreach{
            instance =>
              val address = instance.publicDnsName
              println(s"Uploading $filename to master...")
              rsync(address, file)

              println(s"Uploading $filename to HDFS...")
              val hadoopFS = "ephemeral-hdfs/bin/hadoop fs"
              ssh(address, s"$hadoopFS -rm /$filename;$hadoopFS -put $filename /")
          }
          s"hdfs:///$filename"
        })
        task
      }
    )

  private[this] object EC2Config {
    val region = Region.US_EAST_1
    val clusterName = "my-spark-cluster"
    lazy val ec2 = EC2.at(region)
    def getMaster: Option[Instance] = {
      ec2.instances.find(_.securityGroups.exists(_.getGroupName == clusterName + "-master"))
    }

    val sshCmd = Seq("ssh",
      "-o", "UserKnownHostsFile=/dev/null",
      "-o", "StrictHostKeyChecking=no")

    def ssh(address: String, command: String): String = {
      (sshCmd ++ Seq(s"root@$address", command)) !!
    }

    def rsync(address: String, file: File): Int = {
      val res = Seq(
        "rsync", "--progress",
        "-ve", sshCmd.mkString(" "),
        file.getAbsolutePath,
        s"root@$address:${file.getName}"
      ) !

      if (res != 0) sys.error("Failed to upload job jar to master.")
      res
    }
  }
}
