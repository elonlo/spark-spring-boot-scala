package com.xmen.dc.configuration

import com.xmen.dc.properties.JobProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class KafkaConfiguration {
  @Autowired
  private val jobProperties: JobProperties = null

  @Bean
  def kafkaParams(): Map[String, Object] = {
    Map[String, Object](
      "bootstrap.servers" -> jobProperties.getBrokers,
      "key.deserializer" -> jobProperties.getKeyDeserializer,
      "value.deserializer" -> jobProperties.getValueDeserializer,
      "group.id" -> jobProperties.getGroupId,
      "auto.offset.reset" -> jobProperties.getAutoOffsetReset,
      "enable.auto.commit" -> jobProperties.getEnableAutoCommit
    )
  }
}
