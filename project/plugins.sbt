libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.6")

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")
