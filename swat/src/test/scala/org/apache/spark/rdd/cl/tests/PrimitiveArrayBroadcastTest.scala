package org.apache.spark.rdd.cl.tests

import java.util.LinkedList
import com.amd.aparapi.internal.writer.ScalaArrayParameter
import org.apache.spark.rdd.cl.SyncCodeGenTest
import org.apache.spark.rdd.cl.CodeGenTest
import org.apache.spark.rdd.cl.CodeGenTests
import com.amd.aparapi.internal.model.HardCodedClassModels

import org.apache.spark.broadcast.Broadcast

object PrimitiveArrayBroadcastTest extends SyncCodeGenTest[Int, Int] {
  def getExpectedException() : String = { return null }

  def getExpectedKernel() : String = { getExpectedKernelHelper(getClass) }

  def getExpectedNumInputs() : Int = {
    1
  }

  def init() : HardCodedClassModels = { new HardCodedClassModels() }

  def complete(params : LinkedList[ScalaArrayParameter]) { }

  def getFunction() : Function1[Int, Int] = {
    val broadcast : Broadcast[Array[Int]] = null

    new Function[Int, Int] {
      override def apply(in : Int) : Int = {
        broadcast.value(in)
      }
    }
  }
}
