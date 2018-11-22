package com.xmen.dc.database

import org.apache.spark.sql.SparkSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import scala.collection.mutable.ListBuffer

/**
  * 缓存Hive表服务
  */
@Service
class CacheTableService {
  @Autowired
  private val spark:SparkSession = null

  private val tables = new ListBuffer[String]()

  /**
    * 缓存表
    * @param name
    */
  def addTable(name: String): Unit = {
    spark.sqlContext.sql(s"CACHE LAZY TABLE $name")
    tables.append(name)
  }

  /**
    * 取消缓存
    * @param name
    */
  def removeTable(name: String): Unit = {
    spark.sqlContext.sql(s"UNCACHE TABLE $name")
    tables.dropWhile(_.equals(name))
  }

  /**
    * 取消所有表的缓存
    */
  def removeAll(): Unit = {
    tables.foreach(x => spark.sqlContext.sql(s"UNCACHE TABLE $x"))
  }
}
