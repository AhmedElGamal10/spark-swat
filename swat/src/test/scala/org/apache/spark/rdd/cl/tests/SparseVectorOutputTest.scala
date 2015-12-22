package org.apache.spark.rdd.cl.tests

import java.util.LinkedList

import com.amd.aparapi.internal.writer.ScalaArrayParameter
import com.amd.aparapi.internal.model.Tuple2ClassModel
import com.amd.aparapi.internal.model.ClassModel
import com.amd.aparapi.internal.model.HardCodedClassModels
import com.amd.aparapi.internal.model.SparseVectorClassModel

import org.apache.spark.rdd.cl.SyncCodeGenTest
import org.apache.spark.rdd.cl.CodeGenTest
import org.apache.spark.rdd.cl.CodeGenTests
import org.apache.spark.rdd.cl.CodeGenUtil

import org.apache.spark.mllib.linalg.SparseVector
import org.apache.spark.mllib.linalg.Vectors

import org.apache.spark.rdd.cl.SparseVectorInputBufferWrapperConfig

object SparseVectorOutputTest extends SyncCodeGenTest[Int, SparseVector] {
  def getExpectedException() : String = { return null }

  def getExpectedKernel() : String = { getExpectedKernelHelper(getClass) }

  def getExpectedNumInputs : Int = {
    1
  }

  def init() : HardCodedClassModels = {
    val models = new HardCodedClassModels()
    val sparseVectorModel : SparseVectorClassModel =
            SparseVectorClassModel.create()
    models.addClassModelFor(classOf[SparseVector], sparseVectorModel)
    models
  }

  def complete(params : LinkedList[ScalaArrayParameter]) {
  }

  def getFunction() : Function1[Int, SparseVector] = {
    new Function[Int, SparseVector] {
      override def apply(in : Int) : SparseVector = {
        val indicesArr = new Array[Int](in)
        val valuesArr = new Array[Double](in)

        var i = 0
        while (i < in) {
          indicesArr(i) = 10 * in
          valuesArr(i) = 20 * in
          i += 1
        }
        Vectors.sparse(in, indicesArr, valuesArr).asInstanceOf[SparseVector]
      }
    }
  }
}
