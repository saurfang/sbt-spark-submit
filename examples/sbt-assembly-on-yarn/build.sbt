import sbtassembly.AssemblyPlugin.autoImport._
import sbt.Attributed._
import sbtsparksubmit.SparkSubmitPlugin.autoImport._

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-yarn" % "1.4.0" % "provided" excludeAll ExclusionRule(organization = "org.apache.hadoop"),
  "org.apache.hadoop" % "hadoop-client" % "2.4.0" % "provided",
  "org.apache.hadoop" % "hadoop-yarn-client" % "2.4.0" % "provided"
)

//defaults to yarn-cluster if approriate
sparkSubmitMaster := {
  (sparkArgs, appArgs) =>
    if(appArgs.contains("--help"))
      "local"
    else
      "yarn-cluster"
}
//supply default spark configuration
sparkSubmitPropertiesFile := Some(s"${(Keys.resourceDirectory in Compile).value}/spark-defaults.conf")
//make sure yarn-site.xml is on classpath
sparkSubmitClasspath := {
  new File(sys.env.getOrElse("HADOOP_CONF_DIR", "")) +:
    data((fullClasspath in Compile).value)
}
//submit the assembly jar with all dependencies
sparkSubmitJar := assembly.value
//now blend in the sparkSubmit settings
SparkSubmit.settings
