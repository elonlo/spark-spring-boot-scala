package com.xmen.dc.manager

import com.xmen.dc.properties.KafkaOffsetProperties
import kafka.utils.ZkUtils
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange
import scala.collection.mutable

/**
  * Kafka Offset管理类
  * @param connectManager
  * @param properties
  */
class KafkaOffsetManager(connectManager: MysqlConnectManager, properties: KafkaOffsetProperties) {
  /**
    * 保存消费者的分区偏移量信息
    * @param groupId
    * @param offsetsRanges
    */
  def save(groupId: String, offsetsRanges: Array[OffsetRange]) = {
    if (offsetsRanges.size <= 0) {
      println("Offset偏移信息为空，保存失败")
    } else {
      val connection = connectManager.getConnection()
      val stmt = connection.createStatement()
      for (offset <- offsetsRanges) {
        val sql = "insert into spark.kafka_consumer_offsets (group_id,topic,`partition`,until_offset,insert_time) " +
          s"values('$groupId','${offset.topic}',${offset.partition},${offset.untilOffset},now()) " +
          s"ON DUPLICATE KEY UPDATE until_offset=${offset.untilOffset},insert_time=now()"
        stmt.addBatch(sql)
      }
      stmt.executeBatch()
      connection.commit()
      connectManager.closeConnect(null, stmt, connection)
    }
  }

  /**
    * 获取消费者的历史分区偏移信息
    * @param groupId
    * @return
    */
  def get(groupId: String, topics: Seq[String]): Option[Map[TopicPartition, Long]] = {
    val connection = connectManager.getConnection()
    val stmt = connection.createStatement()
    val sql = s"select * from spark.kafka_consumer_offsets where group_id='$groupId'"
    val rs = stmt.executeQuery(sql)
    val partitionMap = new mutable.HashMap[TopicPartition, Long]()
    while (rs.next()) {
      val partition = new TopicPartition(rs.getString("topic"), rs.getInt("partition"))
      partitionMap.put(partition, rs.getLong("until_offset"))
    }

    connectManager.closeConnect(rs, stmt, connection)

    if (partitionMap.size <= 0) {
      return None
    }

    // 获取消费者最新的分区信息
    val (zkClient, zkConnection) = ZkUtils.createZkClientAndConnection(properties.getZkAddress, 30000, 30000)
    val zkUtils = new ZkUtils(zkClient, zkConnection, false)
    val  topicPartitions = zkUtils.getPartitionsForTopics(topics)
    for (topic <- topics) {
      val partitions = topicPartitions(topic)
      for (partition <- partitions) {
        val tmpTopicPartition = new TopicPartition(topic, partition)
        if (!partitionMap.contains(tmpTopicPartition)) {
          partitionMap.put(tmpTopicPartition, 0)
        }
      }
    }

    Some(partitionMap.toMap)
  }
}
