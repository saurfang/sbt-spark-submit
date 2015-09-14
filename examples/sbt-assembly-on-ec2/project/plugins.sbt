lazy val root = Project("plugins", file(".")).dependsOn(plugin)

lazy val plugin = file("../../").getCanonicalFile.toURI

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")

libraryDependencies ++= Seq(
  "com.github.seratch" %% "awscala"          % "0.5.3" excludeAll ExclusionRule(organization = "com.amazonaws"),
  "com.amazonaws"      %  "aws-java-sdk-s3"  % "1.10.1",
  "com.amazonaws"      %  "aws-java-sdk-ec2" % "1.10.1"
)
