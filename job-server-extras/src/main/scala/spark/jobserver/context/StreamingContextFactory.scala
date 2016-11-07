package spark.jobserver.context

import com.typesafe.config.Config
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import spark.jobserver.{ContextLike, SparkStreamingJob, api}

class StreamingContextFactory extends ScalaContextFactory {

  type C = StreamingContext with ContextLike

  def isValidJob(job: api.SparkJobBase): Boolean = job.isInstanceOf[SparkStreamingJob]

  def makeContext(sparkConf: SparkConf, config: Config,  contextName: String): C = {
    val interval = config.getInt("streaming.batch_interval")
    val stopGracefully = config.getBoolean("streaming.stopGracefully")
    val stopSparkContext = config.getBoolean("streaming.stopSparkContext")
    val ss = SparkSession.builder.config(sparkConf).getOrCreate()

    new StreamingContext(sparkConf, Milliseconds(interval)) with ContextLike {
      def stop() {
        //Gracefully stops the spark context
        stop(stopSparkContext, stopGracefully)
      }

      override def sparkSession: SparkSession = ss
      override def sparkContext = ss.sparkContext
    }
  }
}
