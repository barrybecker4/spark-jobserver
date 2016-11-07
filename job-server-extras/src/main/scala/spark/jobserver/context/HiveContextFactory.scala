package spark.jobserver.context

import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
//import org.apache.spark.sql.hive.HiveContext
import spark.jobserver.{ContextLike, SparkHiveJob, api}

class HiveContextFactory extends ScalaContextFactory {
  type C = SparkContext with ContextLike

  def isValidJob(job: api.SparkJobBase): Boolean = job.isInstanceOf[SparkHiveJob]

  def makeContext(sparkConf: SparkConf, config: Config,  contextName: String): C = {
    contextFactory(sparkConf)
  }

  protected def contextFactory(conf: SparkConf): C = {
    val ss = SparkSession.builder.enableHiveSupport().config(conf).getOrCreate()
    new SparkContext(conf) with ContextLike {
      def sparkSession = ss
      override def stop() { this.sparkSession.stop() }
    }
  }
}

/*
private[jobserver] trait HiveContextLike extends ContextLike {
  def stop() { this.sparkSession.stop() }
}*/
