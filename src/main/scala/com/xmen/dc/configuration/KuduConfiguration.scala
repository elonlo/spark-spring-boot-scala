package com.xmen.dc.configuration

import com.xmen.dc.manager.KuduManager
import com.xmen.dc.properties.JobProperties
import org.apache.commons.lang3.StringUtils
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.SparkSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class KuduConfiguration {
  @Autowired
  private val jobProperties: JobProperties = null

  @Autowired
  private val spark:SparkSession = null

  @Bean
  def createKuduContext(): KuduContext = {
    if (StringUtils.isNotBlank(jobProperties.getKuduMaster)) {
      return new KuduContext(jobProperties.getKuduMaster, spark.sparkContext)
    }
    sys.error("创建KUDU上下文失败，请检查KUDU配置是否正确！")
    null
  }

  @Bean
  def createKuduManager(): KuduManager = {
    val context = createKuduContext()
    if (context != null) {
      new KuduManager(context)
    } else {
      sys.error("创建KUDU管理类失败，请检查KUDU配置是否正确！")
      null
    }
  }
}
