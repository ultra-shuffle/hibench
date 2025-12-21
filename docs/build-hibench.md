### Maven ###

HiBench is built with Maven. If `mvn` is not installed, you can use Spark’s bundled wrapper from this workspace:

    MVN="$SPARK_HOME/build/mvn"

If you have system Maven, use:

    MVN=mvn

All commands below assume you run them from the HiBench root directory:

    HIBENCH_HOME=/path/to/HiBench-7.1.1
    cd "$HIBENCH_HOME"

### Build SparkBench (recommended) ###

This builds the Spark workloads (and the `sparkbench-assembly-7.1.1-dist.jar` that the `bin/workloads/**/spark/run.sh`
scripts submit).

    cd "$HIBENCH_HOME/sparkbench"
    $MVN -Dspark=3.5 -Dscala=2.13 -DskipTests clean package

Note: if you run Maven from the HiBench root with `-pl sparkbench`, Maven will only build the `sparkbench` *aggregator POM*
and you will see `No sources to compile`. Building from `sparkbench/` (as above) ensures the actual workload modules are built.

If you previously built for Spark 2.4 / Scala 2.12 and see runtime errors like:

    java.lang.NoSuchMethodError: scala.Predef$.refArrayOps

it means you are running a Scala 2.12-built jar on a Scala 2.13 Spark; run the `clean package` command above to rebuild.

### Build only one SparkBench module (faster) ###

By default SparkBench builds a set of modules (micro/ml/websearch/graph/sql). To build only one module, set the `modules`
property and enable a module profile.

Example: build only `micro` (wordcount/sort/terasort/repartition/sleep):

    cd "$HIBENCH_HOME/sparkbench"
    $MVN -Dmodules -Pmicro -Dspark=3.5 -Dscala=2.13 -DskipTests clean package

Example: build only `sql`:

    cd "$HIBENCH_HOME/sparkbench"
    $MVN -Dmodules -Psql -Dspark=3.5 -Dscala=2.13 -DskipTests clean package

### Build Structured Streaming (optional) ###

Structured Streaming is not built by default. Enable it explicitly:

    cd "$HIBENCH_HOME/sparkbench"
    $MVN -Dmodules -PstructuredStreaming -Dspark=3.5 -Dscala=2.13 -DskipTests clean package

### Spark Streaming (legacy) ###

The `sparkbench/streaming` module uses the old Spark Streaming Kafka 0.8 integration and is not supported on Spark 3.x in this repo.

### Build All ###
To simply build all modules in HiBench, use the below command. This could be time consuming because the hadoopbench relies on 3rd party tools like Mahout and Nutch. The build process automatically downloads these tools for you. If you won't run these workloads, you can only build a specific framework to speed up the build process.

    $MVN -Dspark=3.5 -Dscala=2.13 -DskipTests clean package


### Build a specific framework benchmark ###
HiBench 6.0 supports building only benchmarks for a specific framework. For example, to build the Hadoop benchmarks only, we can use the below command:

    $MVN -Phadoopbench -Dspark=3.5 -Dscala=2.13 -DskipTests clean package

To build Hadoop and spark benchmarks

    $MVN -Phadoopbench -Psparkbench -Dspark=3.5 -Dscala=2.13 -DskipTests clean package

Supported frameworks includs: hadoopbench, sparkbench, flinkbench, stormbench, gearpumpbench.

### Specify Scala Version ###
To specify the Scala version, use `-Dscala=xxx` (2.10, 2.11, 2.12, 2.13). In this workspace the default is Scala 2.13.

    $MVN -Dscala=2.13 -DskipTests clean package
tips:
Because some Maven plugins cannot support Scala version perfectly, there are some exceptions.

1. No matter what Scala version is specified, the module (gearpumpbench/streaming) is always built in Scala 2.11.
2. `sparkbench/streaming` targets the legacy Spark Streaming Kafka 0.8 integration and is not supported on Spark 3.x in this repo.



### Specify Spark Version ###
To specify the spark version, use `-Dspark=xxx` (1.6, 2.0, 2.1, 2.2, 2.3, 2.4, 3.5). In this workspace the default is Spark 3.5.

    $MVN -Psparkbench -Dspark=3.5 -Dscala=2.13 -DskipTests clean package
tips:
when the spark version is specified to spark2.0(1.6) , the scala version will be specified to scala2.11(2.10) by
default . For example , if we want use spark2.0 and scala2.11 to build hibench. we just use the command `mvn -Dspark=2.0 clean
package` , but for spark2.0 and scala2.10 , we need use the command `mvn -Dspark=2.0 -Dscala=2.10 clean package` .
Similarly , the spark1.6 is associated with the scala2.10 by default.

### Build a single module ###
If you are only interested in a single workload in HiBench. You can build a single module. For example, the below command only builds the SQL workloads for Spark.

    $MVN -Psparkbench -Dmodules -Psql -Dspark=3.5 -Dscala=2.13 -DskipTests clean package

Supported modules includes: micro, ml(machine learning), sql, websearch, graph, streaming, structuredStreaming(spark 2.0 or higher) and dal.

### Build Structured Streaming ###
For Spark 2.0 or higher versions, we add the benchmark support for Structured Streaming. This is a new module which cannot be compiled in Spark 1.6. And it won't get compiled by default even if you specify the spark version as 2.0 or higher. You must explicitly specify it like this:

    $MVN -Psparkbench -Dmodules -PstructuredStreaming -Dspark=3.5 -Dscala=2.13 -DskipTests clean package 

### Build DAL on Spark ###
By default the dal module will not be built and needs to be enabled explicitly by adding "-Dmodules -Pdal", for example:

    $MVN -Psparkbench -Dmodules -Pml -Pdal -Dspark=3.5 -Dscala=2.13 -DskipTests clean package

Currently there is only one workload KMeans available in DAL. To run the workload, install DAL and setup the environment by following https://github.com/intel/daal
