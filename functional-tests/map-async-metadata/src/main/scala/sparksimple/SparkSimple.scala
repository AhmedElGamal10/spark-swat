import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.cl._
import Array._
import scala.math._
import org.apache.spark.rdd._
import java.net._

object SparkSimple {
    def main(args : Array[String]) {
        if (args.length < 1) {
            println("usage: SparkSimple cmd")
            return;
        }

        val cmd = args(0)

        if (cmd == "convert") {
            convert(args.slice(1, args.length))
        } else if (cmd == "run") {
            run_simple(args.slice(2, args.length), args(1).toBoolean)
        } else if (cmd == "check") {
            val correct : Array[Tuple2[Double, Option[Double]]] = run_simple(
                    args.slice(1, args.length), false)
            val actual : Array[Tuple2[Double, Option[Double]]] = run_simple(
                    args.slice(1, args.length), true)
            assert(correct.length == actual.length)
            for (i <- 0 until correct.length) {
                val a = correct(i)._1
                val b = actual(i)._1
                if (a != b) {
                    System.err.println(i + ": expected=" + a + ", actual=" + b)
                    System.exit(1)
                }

                val m1 = correct(i)._2.get
                val m2 = correct(i)._2.get
                if (m1 != m2) {
                    System.err.println(i + ": metadata expected=" + m1 + ", actual=" + m2)
                    System.exit(1)
                }
            }
            System.err.println("PASSED")
        }
    }

    def get_spark_context(appName : String) : SparkContext = {
        val conf = new SparkConf()
        conf.setAppName(appName)

        val localhost = InetAddress.getLocalHost
        conf.setMaster("spark://" + localhost.getHostName + ":7077") // 7077 is the default port

        return new SparkContext(conf)
    }

    def run_simple(args : Array[String], useSwat : Boolean) : Array[Tuple2[Double, Option[Double]]] = {
        if (args.length != 1) {
            println("usage: SparkSimple run input-path");
            println(" nargs=" + args.length)
            return new Array[Tuple2[Double, Option[Double]]](0);
        }
        val sc = get_spark_context("Spark Simple");

        val m = 4
        val inputPath = args(0)
        val inputs_raw : RDD[Double] = sc.objectFile[Double](inputPath).cache

        val inputs = CLWrapper.cl[Double](inputs_raw, useSwat)

        val outputs1 : RDD[Tuple2[Double, Option[Double]]] = inputs.mapAsync(
                (v: Double, stream: AsyncOutputStream[Double, Double]) => {
                  stream.spawn(() => v, Some(v + 3.0))
                })

        val outputs : Array[Tuple2[Double, Option[Double]]] = outputs1.collect
        sc.stop
        outputs
    }

    def convert(args : Array[String]) {
        if (args.length != 2) {
            println("usage: SparkSimple convert input-dir output-dir");
            return
        }
        val sc = get_spark_context("Spark KMeans Converter");

        val inputDir = args(0)
        var outputDir = args(1)
        val input = sc.textFile(inputDir)
        val converted = input.map(line => {
            line.toDouble })
        converted.saveAsObjectFile(outputDir)
    }
}
