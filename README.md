# sbt-spark-submit

[![Build Status](https://travis-ci.org/saurfang/sbt-spark-submit.svg?branch=master)](https://travis-ci.org/saurfang/sbt-spark-submit)

This sbt plugin provides customizable sbt tasks to fire Spark jobs against local or remote Spark clusters.
It allows you submit Spark applications without leaving your favorite development environment.
The reactive nature of sbt makes it possible to integrate this with your Spark clusters whether it is a standalone
cluster, YARN cluster, clusters run on EC2 and etc.
