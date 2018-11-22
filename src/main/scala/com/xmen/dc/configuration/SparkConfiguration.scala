package com.xmen.dc.configuration

import com.xmen.dc.filter.TempFileInputFilter
import com.xmen.dc.properties.JobProperties
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class SparkConfiguration {

  @Autowired
  private val jobProperties: JobProperties = null

  @Bean
  def sparkConf(): SparkConf = {
    new SparkConf()
      .setAppName(jobProperties.getName)
      .set("spark.streaming.stopGracefullyOnShutdown", "true")
      .set("spark.hadoop." + FileInputFormat.PATHFILTER_CLASS, classOf[TempFileInputFilter].getName)
  }

  @Bean
  def sparkSession(): SparkSession = {
    SparkSession.builder()
      .config(sparkConf())
      .getOrCreate()
  }

  @Bean
  def streamingContext(): StreamingContext = {
    new StreamingContext(sparkSession().sparkContext, Seconds(jobProperties.getDuration))
  }
}
