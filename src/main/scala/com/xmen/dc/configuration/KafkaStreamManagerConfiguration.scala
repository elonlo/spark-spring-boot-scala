package com.xmen.dc.configuration

import com.xmen.dc.manager.{KafkaOffsetManager, KafkaStreamManager}
import com.xmen.dc.properties.JobProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class KafkaStreamManagerConfiguration {
  @Autowired
  private val manager: KafkaOffsetManager = null
  @Autowired
  private val sparkConfiguration: SparkConfiguration = null
  @Autowired
  private val jobProperties: JobProperties = null
  @Autowired
  private val kafkaConfiguration: KafkaConfiguration = null

  @Bean
  def createKafkaStreamManager: KafkaStreamManager = {
    new KafkaStreamManager(jobProperties, sparkConfiguration, kafkaConfiguration, manager)
  }
}
