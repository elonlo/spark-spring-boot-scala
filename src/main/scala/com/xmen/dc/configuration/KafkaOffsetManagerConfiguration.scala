package com.xmen.dc.configuration

import com.xmen.dc.manager.{MysqlConnectManager, KafkaOffsetManager}
import com.xmen.dc.properties.KafkaOffsetProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class KafkaOffsetManagerConfiguration {
  @Autowired
  private val properties: KafkaOffsetProperties = null

  @Autowired
  private val connectManager: MysqlConnectManager = null

  @Bean
  def createKafkaOffsetManager(): KafkaOffsetManager = {
    new KafkaOffsetManager(connectManager, properties)
  }
}
