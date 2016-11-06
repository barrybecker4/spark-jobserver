package spark.jobserver

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark._
import org.apache.spark.sql.SparkSession

/**
 * A very short job for stress tests purpose.
 * Small data. Double every value in the data.
 */
object VeryShortDoubleJob extends SparkJob {
  private val data = Array(1, 2, 3)

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[4]").setAppName("VeryShortDoubleJob")
    val sparkSession = SparkSession.builder.config(conf).getOrCreate() //new SparkContext(conf)
    val sc = sparkSession.sparkContext
    val config = ConfigFactory.parseString("")
    val results = runJob(sc, config)
    println("Result is " + results)
  }

  override def validate(sc: SparkContext, config: Config): SparkJobValidation = {
    SparkJobValid
  }

  override def runJob(sc: SparkContext, config: Config): Any = {
    val dd = sc.parallelize(data)
    dd.map( _ * 2 ).collect()
  }
}
