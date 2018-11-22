package com.xmen.dc.manager

import com.xmen.dc.properties.RedisPoolProperties
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

/**
  * Redis连接池管理
  * @param properties
  */
class RedisConnectManager(properties: RedisPoolProperties) extends Serializable{
  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = {
      closePool()
    }
  })

  private var jedisPool: JedisPool = _
  private val local: ThreadLocal[Jedis] = new ThreadLocal[Jedis]()

  def getConnection(): Jedis = {
    var jedis = local.get()
    if (jedis == null) {
      if (jedisPool == null) initialPool()
      jedis = jedisPool.getResource
      local.set(jedis)
    }
    jedis
  }

  def close(): Unit = {
    val jedis = local.get()
    if (jedis != null) {
      jedis.close()
    }
    local.set(null)
  }

  def closePool(): Unit = {
    if (jedisPool != null) {
      jedisPool.close()
      jedisPool = null
    }
  }

  private def initialPool(): Unit = {
    if (jedisPool != null) return
    try {
      val config = new JedisPoolConfig
      config.setMaxTotal(properties.getMaxActive)
      config.setMaxIdle(properties.getMaxIdle)
      config.setMaxWaitMillis(properties.getMaxWait)
      config.setTestOnBorrow(properties.getTestOnBorrow)
      config.setTestOnReturn(properties.getTestOnReturn)
      jedisPool = new JedisPool(config,
        properties.getIp,
        properties.getPort,
        properties.getTimeout)
      println("Redis线程池被成功初始化")
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
