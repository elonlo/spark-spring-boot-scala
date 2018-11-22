package com.xmen.dc.manager

import com.xmen.dc.configuration.{KafkaConfiguration, SparkConfiguration}
import com.xmen.dc.properties.JobProperties
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies, OffsetRange}

class KafkaStreamManager(jobProperties: JobProperties,
                         sparkConfiguration: SparkConfiguration,
                         kafkaConfiguration: KafkaConfiguration,
                         manager: KafkaOffsetManager) {
  /**
    * 创建流接收对象
    * @param ssc
    * @return
    */
  def createKafkaStream(ssc: StreamingContext) : InputDStream[ConsumerRecord[String, String]] = {
    val topicsSeq = jobProperties.getTopics.split(",").toSeq
    createKafkaStream(ssc, jobProperties.getGroupId, topicsSeq)
  }

  /**
    * 创建流接收对象
    * @param ssc
    * @param topics
    * @return
    */
  def createKafkaStream(ssc: StreamingContext, topics: Seq[String]) : InputDStream[ConsumerRecord[String, String]] = {
    createKafkaStream(ssc, jobProperties.getGroupId, topics)
  }

  /**
    * 创建流接收对象
    * @param ssc
    * @param topics
    * @return
    */
  def createKafkaStream(ssc: StreamingContext,
                        groupId: String,
                        topics: Seq[String]) : InputDStream[ConsumerRecord[String, String]] = {
    val offsetDatas = manager.get(groupId, topics)
    offsetDatas.foreach(println)

    val stream = offsetDatas match {
      case None =>
        println("系统第一次启动，没有读取到偏移量，默认就最新的offset开始消费")
        //使用最新的偏移量创建DirectStream
        KafkaUtils.createDirectStream[String, String](
          ssc,
          LocationStrategies.PreferConsistent,
          ConsumerStrategies.Subscribe[String, String](topics, kafkaConfiguration.kafkaParams())
        )
      case Some(lastStopOffset) =>
        println("从Mysql中读取到偏移量，从上次的偏移量开始消费数据")
        KafkaUtils.createDirectStream[String, String](
          ssc,
          LocationStrategies.PreferConsistent,
          ConsumerStrategies.Assign[String, String](lastStopOffset.keys.toList, kafkaConfiguration.kafkaParams(), lastStopOffset)
        )
    }
    stream
  }

  /**
    * 保存kafka流状态信息
    * @param offsetsRanges
    */
  def saveKafkaStreamStatus(offsetsRanges: Array[OffsetRange]): Unit = {
    manager.save(jobProperties.getGroupId, offsetsRanges)
  }
}
