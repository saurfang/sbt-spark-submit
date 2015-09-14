import sbtassembly.AssemblyPlugin.autoImport._
import sbt.Attributed._
import sbtsparksubmit.SparkSubmitPlugin.autoImport._

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.5.0" % "provided"
)

//submit the assembly jar with all dependencies
sparkSubmitJar := assembly.value.getAbsolutePath
//now blend in the sparkSubmit settings
SparkSubmit.settings
