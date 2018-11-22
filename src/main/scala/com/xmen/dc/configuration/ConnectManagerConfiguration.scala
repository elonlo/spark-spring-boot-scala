package com.xmen.dc.configuration

import com.xmen.dc.manager.{MysqlConnectManager, RedisConnectManager, RedisSentinelConnectManager}
import com.xmen.dc.properties.{MysqlPoolProperties, RedisPoolProperties, RedisSentinelProperties}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}


@Configuration
class ConnectManagerConfiguration {
  @Autowired
  private val mysqlProperties: MysqlPoolProperties = null
  @Autowired
  private val redisProperties: RedisPoolProperties = null
  @Autowired
  private val redisSentinelProperties: RedisSentinelProperties = null

  @Bean
  def createMysqlConnectManager(): MysqlConnectManager = {
    new MysqlConnectManager(mysqlProperties)
  }

  @Bean
  def createRedisConnectManager(): RedisConnectManager = {
    new RedisConnectManager(redisProperties)
  }

  @Bean
  def createRedisSentinelConnectManager: RedisSentinelConnectManager = {
    new RedisSentinelConnectManager(redisSentinelProperties)
  }
}
