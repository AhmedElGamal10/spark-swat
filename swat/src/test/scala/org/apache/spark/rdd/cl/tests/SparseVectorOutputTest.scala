/*
Copyright (c) 2016, Rice University

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

1.  Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
2.  Redistributions in binary form must reproduce the above
     copyright notice, this list of conditions and the following
     disclaimer in the documentation and/or other materials provided
     with the distribution.
3.  Neither the name of Rice University
     nor the names of its contributors may be used to endorse or
     promote products derived from this software without specific
     prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

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
