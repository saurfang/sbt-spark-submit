Example project that uses sbt-assembly to package all dependencies and submit to a EC2 standalone cluster.


Launch Spark cluster using [`spark-ec2`](http://spark.apache.org/docs/latest/ec2-scripts.html) script:

```
spark-ec2 -k <aws key pair> -i <aws pem key> -r <region> launch my-spark-cluster --copy-aws-credentials -t m3.large
```

Now submit job as you develop:
 
```
sbt sparkPi-ec2
```

Application JAR will be rebuilt, submitted to ec2 HDFS. Then a job will be submitted to run as cluster deploy mode.

Note: You need to open up either 7077 or 6066 on master security group so remote submission can be made.
