package spark.jobserver.context

import com.typesafe.config.Config
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SQLContext, SparkSession}
import spark.jobserver.{ContextLike, SparkSqlJob, api}

class SQLContextFactory extends ScalaContextFactory {
  type C = SparkContext with ContextLike

  def isValidJob(job: api.SparkJobBase): Boolean = job.isInstanceOf[SparkSqlJob]

  def makeContext(sparkConf: SparkConf, config: Config,  contextName: String): C = {

    val ss = SparkSession.builder.config(sparkConf).getOrCreate()

    new SparkContext(sparkConf) with ContextLike {
      def sparkSession = ss
      override def stop() { this.sparkContext.stop() }
    }
  }
}