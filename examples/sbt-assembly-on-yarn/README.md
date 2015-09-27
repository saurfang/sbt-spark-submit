Example project that uses sbt-assembly to package all dependencies and submit to a YARN cluster.

Set environment variable `HADOOP_CONF_DIR` or `YARN_CONF_DIR` so `spark-submit` can pick up cluster configuration.

It is highly recommended to point `spark.yarn.jar` to SPARK assembly JAR in `spark-defaults.conf`.
This will avoid uploading the Spark assembly JAR to HDFS every time an application is submitted.