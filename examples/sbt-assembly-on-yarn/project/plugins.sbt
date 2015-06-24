lazy val root = Project("plugins", file(".")).dependsOn(plugin)

lazy val plugin = file("../../").getCanonicalFile.toURI

//addSbtPlugin("com.github.saurfang" % "sbt-spark-submit" % "0.0.1")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")
