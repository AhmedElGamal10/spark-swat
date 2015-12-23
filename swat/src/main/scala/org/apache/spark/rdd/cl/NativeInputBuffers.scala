package org.apache.spark.rdd.cl

trait NativeInputBuffers[T] {
  var id : Int = -1
  var clBuffersReadyPtr : Long = 0L

  def releaseOpenCLArrays()

  /*
   * For use by LambdaOutputBuffer only when reverting to JVM execution due to
   * OOM errors on the accelerator.
   */
  def hasNext() : Boolean
  def next() : T

  // Transfer the aggregated input items to an OpenCL device
  def copyToDevice(argnum : Int, ctx : Long, dev_ctx : Long,
      cacheId : CLCacheID, persistent : Boolean) : Int
}