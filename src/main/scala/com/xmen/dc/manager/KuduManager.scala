package com.xmen.dc.manager

import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.types.StructType

/**
  * kudu管理类
  * @param context
  */
class KuduManager(context: KuduContext) extends Serializable {

  /**
    * 创建KUDU表
    * @param tableName
    * @param schema
    * @param keys
    * @param partitionKeys
    * @param buckets
    * @return
    */
  def createTable(tableName: String,
                  schema: StructType,
                  keys: Seq[String],
                  partitionKeys: java.util.List[String],
                  buckets: Int): Boolean = {
    if (context.tableExists(tableName)) {
      println(s"Kudu table: $tableName already exists")
      return true
    }
    println(s"Create kudu table: $tableName ......")
    val createTableOptions = new CreateTableOptions()
    createTableOptions.addHashPartitions(partitionKeys, buckets).setNumReplicas(3)
    val table = context.createTable(tableName, schema, keys, createTableOptions)
    table != null
  }

  def query(tableName: String, columns: Seq[String]): Unit = {
//    context.kuduRDD()
  }
}
