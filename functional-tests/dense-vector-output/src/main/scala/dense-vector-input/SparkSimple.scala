import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.cl._
import Array._
import scala.math._
import org.apache.spark.rdd._
import java.net._

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.DenseVector

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
            val correct : Array[DenseVector] = run_simple(args.slice(1, args.length), false)
            val actual : Array[DenseVector] = run_simple(args.slice(1, args.length), true)
            assert(correct.length == actual.length)
            for (i <- 0 until correct.length) {
                val a : DenseVector = correct(i)
                val b : DenseVector = actual(i)
                var error : Boolean = false

                if (a.size != b.size) {
                    System.err.println(i + " expected length " + a.size + " but got length " + b.size)
                    error = true
                }

                if (!error) {
                    var j = 0
                    while (j < a.size) {
                        if (a(j) != b(j)) {
                            System.err.println(i + " at index " + j +
                                    ", expected " + a(j) + " but got " + b(j))
                            error = true
                        }
                        j += 1
                    }
                }

                if (error) System.exit(1)
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

    def run_simple(args : Array[String], useSwat : Boolean) : Array[DenseVector] = {
        if (args.length != 1) {
            println("usage: SparkSimple run input-path");
            return new Array[DenseVector](0);
        }
        val sc = get_spark_context("Spark Simple");

        val inputPath = args(0)
        val inputs_raw : RDD[Int] = sc.objectFile[Int](inputPath).cache
        val inputs = if (useSwat) CLWrapper.cl[Int](inputs_raw) else inputs_raw

        val outputs : RDD[DenseVector] = inputs.map(v => {
            val arr = new Array[Double](v)
            var i = 0
            while (i < v) {
              arr(i) = i
              i += 1
            }
            Vectors.dense(arr).asInstanceOf[DenseVector]
          })
        val outputs2 : Array[DenseVector] = outputs.collect
        var i = 0
        while (i < 10) {
          val curr = outputs2(i)
          var j = 0
          while (j < curr.size) {
            System.err.print(curr(j) + " " )
            j += 1
          }
          System.err.println
          i += 1
        }
        System.err.println("...")
        sc.stop
        outputs2
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
            line.toInt })
        converted.saveAsObjectFile(outputDir)
    }
}
