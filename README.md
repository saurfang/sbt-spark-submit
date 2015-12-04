# sbt-spark-submit

[![Join the chat at https://gitter.im/saurfang/sbt-spark-submit](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/saurfang/sbt-spark-submit?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Build Status](https://travis-ci.org/saurfang/sbt-spark-submit.svg?branch=master)](https://travis-ci.org/saurfang/sbt-spark-submit)

This sbt plugin provides customizable sbt tasks to fire Spark jobs against local or remote Spark clusters.
It allows you submit Spark applications without leaving your favorite development environment.
The reactive nature of sbt makes it possible to integrate this with your Spark clusters whether it is a standalone
cluster, [YARN cluster](examples/sbt-assembly-on-yarn), [clusters run on EC2](examples/sbt-assembly-on-ec2) and etc.

## Motivation

As an awesome Scala developer, your Spark development experience is probably as follows:
```bash
# create assembly jar upon code change
sbt assembly
# coffee break as Scala builds
# transfer the jar to a cluster co-located host
scp target/scala-2.10/myproject-version-assembly.jar sparkcluster:myworkspace
# ssh into that launcher host
ssh sparkcluster
cd myworkspace
# fire spark-submit
$SPARK_HOME/bin/spark-submit --class not.memorable.package.applicaiton.class --master yarn --num-executor 10 \
  --conf some.crazy.config=xyz --executor-memory=lotsG \
  myproject-version-assembly.jar \
  <glorious-application-arguments...>
```
But it doesn't have to be that hard. With this plugin you can reduce all above steps into:
```bash
sbt "sparkSubmitMyClass <additional custom app arguments...>"
```

## Setup

For sbt 0.13.6+ add sbt-spark-submit to your `project/plugins.sbt` or `~/.sbt/0.13/plugins/plugins.sbt` file:

```scala
addSbtPlugin("com.github.saurfang" % "sbt-spark-submit" % "0.0.4")
```

Naturally you will need to have spark dependency in your project itself such as:

```scala
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.4.0" % "provided"
```

`"provided"` is recommended as Spark is pretty huge and you don't need to include in your fat jar during deployment.

### YARN

If you are running on YARN, you also need to add [spark-yarn](http://mvnrepository.com/artifact/org.apache.spark/spark-yarn_2.10).
For example:

```scala
libraryDependencies += "org.apache.spark" %% "spark-yarn" % "1.4.0" % "provided"
```

If you are submitting cross platform (e.g. from Windows to Linux), you need Hadoop 2.4+ which support platform
neutral classpath separator. In those cases, you might need to exclude Hadoop dependencies from Spark first. For example:
```scala
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-yarn" % "1.4.0" % "provided" excludeAll ExclusionRule(organization = "org.apache.hadoop"),
  "org.apache.hadoop" % "hadoop-client" % "2.4.0" % "provided",
  "org.apache.hadoop" % "hadoop-yarn-client" % "2.4.0" % "provided"
)
```

Finally you should use 
```scala
enablePlugins(SparkSubmitYARN)
```
to enable default YARN settings. This defaults the master to `yarn-cluster` whenever appropriate and append 
`HADOOP_CONF_DIR/YARN_CONF_DIR` to launcher classpath so YARN resource manager can be correctly determined. 
See below for more details.

## Feature

This AutoPlugin automatically adds a `sparkSubmit` task to every project in your build, the usage is as follows:
```shell
sbt "sparkSubmit <spark arguments> -- <application arguments>"
```
For example
```shell
sbt "sparkSubmit --class SparkPi --"
sbt "sparkSubmit --class SparkPi -- 10"
sbt "sparkSubmit --master local[2] --class SparkPi --"
sbt "sparkSubmit myarguments"
```

You can also define specialized SparkSubmit task, we recommend create a `project/SparkSubmit.scala`:
```scala
import sbtsparksubmit.SparkSubmitPlugin.autoImport._

object SparkSubmit {
  lazy val settings =
    SparkSubmitSetting("sparkPi",
      Seq("--class", "SparkPi")
    )
}
```
Then in the `build.sbt`, import the settings by:
```scala
SparkSubmit.settings
```
With that you just gained a new sbt task called `sparkPi` which you can run by `sbt sparkPi`. 
The task automatically recompiles and repackages the JAR as needed. It starts the SparkPi example in local
mode. You can change the default Spark master by specifying `--master` as you would with *spark-submit*.
You can embed default Spark and/or Application arguments in the sbt task to cover you most common
use cases. Please see [below](#define-custom-sparksubmit-task) for more details for custom spark-submit task.


Below we go into details about various keys that controls the default behavior of this task.


### Application JAR
`sparkSubmitJar` specifies the application JAR used in submission. By default this is simply the JAR created by
`package` task. This will be sufficient to run in local mode.

More advanced techniques include but not limited to:

1. Use one-jar plugins such as `sbt-assembly` to create a fat jar for deployment.
2. While YARN automatically uploads the application jar, it doesn't seem to be the case for Spark Standalone
cluster. So you can inject a JAR uploading process inside this key and returns the uploaded JAR instead. See
[sbt-assembly-on-ec2](examples/sbt-assembly-on-ec2) for an example.

### Spark and Application Arguments
`sparkSubmitSparkArgs` and `sparkSubmitAppArgs` represents the arguments for Spark and Application respectively.
Spark arguments are things like `--class`, `--conf` and etc. Application arguments are for the Spark application
being submitted.

### Application Master
`sparkSubmitMaster` specifies the default master to use if `--master` is not already supplied. This takes a function
of the form `(sparkArgs: Seq[String], appArgs: Seq[String]) => String`. By default it blindly maps to `local`.

More interesting ones may be:

1. If there is `--help` in `appArgs` you will want to run as `local` to see the usage information immediately.
2. For YARN deployment, `yarn-cluster` is appropriate especially if you are submitting to a remote cluster from IDE.
3. For EC2 deployment, you can use `spark-ec2` script to figure out the correct address of Spark master. See
[sbt-assembly-on-ec2](examples/sbt-assembly-on-ec2) for an example.

### Default Properties File
`sparkSubmitPropertiesFile` specifies the default properties file to use if `--properties-file` is not already supplied.

This can be especially useful for YARN deployment by pointing the Spark assembly to a JAR on HDFS via `spark.yarn.jar`
property so as to avoid the overhead of uploading Spark assembly jar every time application is submitted. See
[sbt-assembly-on-ec2](examples/sbt-assembly-on-yarn) for an example.

Other interesting settings include driver/executor memory/cores, RDD compression/serialization and etc.

### Classpath
`sparkSubmitClassPath` sets the classpath to use for Spark application deployment. Currently this is only relevant for
YARN deployment as I couldn't get `yarn-site.xml` correctly picked up even when `HADOOP_CONF_DIR` is properly set.
In this case, you can add:
```scala
sparkSubmitClasspath := {
  new File(sys.env.getOrElse("HADOOP_CONF_DIR", "")) +:
    data((fullClasspath in Compile).value)
}
```
Note: This is already automatically injected once you `enablePlugins(SparkSubmitYARN)`

### SparkSubmit inputKey
`sparkSubmit` is a generic `inputKey` and we will show you how to define additional tasks that have
different default behavior in terms of parameters. As for the inputKey itself, it parses
space delimited arguments. If `--` is present, the former part gets appended to `sparkSubmitSparkArgs` and
the latter part gets appended to `sparkSubmitAppArgs`. If `--` is missing, then all arguments are assumed
to be application arguments.

If `--master` is missing in `sparkSubmitSparkArgs`, then `sparkSubmitMaster` is used to assign a default
application master.

If `--properties-file` is missing in `sparkSubmitSparkArgs` and `sparkSubmitPropertiesFile` is not `None`,
then it will be included.

Finally it runs the Spark application deploy process using the specified Classpath and specified JAR with
above mentioned arguments.



## Define Custom SparkSubmit Task
To define specialized SparkSubmit task, we recommend create `project/SparkSubmit.scala`:
```scala
import sbtsparksubmit.SparkSubmitPlugin.autoImport._

object SparkSubmit {
  lazy val settings =
    SparkSubmitSetting("sparkPi",
      Seq("--class", "SparkPi")
    )
}
```

Here we created a single `SparkSubmitSetting` object and fuses it with additional settings.


To create multiple tasks, you can wrap them with `SparkSubmitSetting` again like this:
```scala
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
```

Notice here are two differently named tasks run the same class but with different application arguments.

Of course, you can still append additional arguments in this task. For example:
```shell
sbt "spark2 hello.txt"
sbt spark2Other
```
would be equivalent.

`SparkSubmitSetting` has three `apply` functions:
```scala
def apply(name: String): SparkSubmitSetting
def apply(name: String, sparkArgs: Seq[String] = Seq(), appArgs: Seq[String] = Seq()): SparkSubmitSetting
def apply(sparkSubmitSettings: SparkSubmitSetting*): Seq[Def.Setting[_]]
```
The first creates a simple `SparkSubmitSetting` object with a custom task name. The object itself has `setting` function
that allows you to blend in additional settings that is specific to this task.

Because the most common use case of custom task is to provide custom default Spark and Application arguments,
the second variant allow you provide those directly.

There is already an implicit conversion from `SparkSubmitSetting` to `Seq[Def.Setting[_]]` which allows you to
append itself to your project. When there are multiple settings, the third variant allows you to aggregate all
of them without additional type hinting for implicit to work.

See [`src/sbt-test/sbt-spark-submit/multi-main`](src/sbt-test/sbt-spark-submit/multi-main) for examples.

## Multi-project builds

If you are really awesome to have a multi-project builds, be careful that `sbt sparkSubmit` will trigger aggregation
thus firing multiple instances each for every project. You can do `sbt projectA/sparkSubmit` to restrict the project
scope.

However if you define additional sparkSubmit tasks with unique names, this becomes very friendly. For example,
say you have two projects `A` and `B`, for which you define `sparkA1`, `sparkA2` and `sparkB` tasks respectively.
As long as you attach the `sparkA1` and `sparkA2` to project `A` and `sparkB` to project `B`, `sbt sparkA1` and `sbt sparkA2`
will correctly trigger build on project A while `sparkB` will do the same for project `B` even though you didn't
select any specific project. 

Of course, `sparkB` task won't even trigger a build on `A` unless `B` depends on `A` thanks to the magic of sbt.

See [`src/sbt-test/sbt-spark-submit/multi-project`](src/sbt-test/sbt-spark-submit/multi-project) for examples.

## Resources

For more information and working examples, see projects under [`examples`](examples) and [`src/sbt-test`](src/sbt-test).
