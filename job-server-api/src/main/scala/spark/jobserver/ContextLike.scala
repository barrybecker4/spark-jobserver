package spark.jobserver

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
//import spark.jobserver.context.SparkContextFactory

/**
 * Represents a context based on SparkSession.  Examples include:
 * StreamingContext, HiveContext, SQLContext.
 *
 * The Job Server can spin up not just a vanilla SparkContext, but anything that
 * implements ContextLike.
 */
trait ContextLike {
  /**
   * The underlying SparkContext
   */
  def sparkSession: SparkSession

  def sparkContext: SparkContext = sparkSession.sparkContext

  /**
   * Responsible for performing any cleanup, including calling the underlying context's
   * stop method.
   */
  def stop()
}