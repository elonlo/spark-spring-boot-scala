package com.xmen.dc.manager

import com.xmen.dc.properties.RedisSentinelProperties
import redis.clients.jedis.{Jedis, JedisSentinelPool}

import scala.collection.mutable

class RedisSentinelConnectManager(properties: RedisSentinelProperties) extends Serializable {
  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = {
      closePool()
    }
  })

  private var pool: JedisSentinelPool = _
  private val local: ThreadLocal[Jedis] = new ThreadLocal[Jedis]()

  def getConnection(): Jedis = {
    var jedis = local.get()
    if (jedis == null) {
      if (pool == null) initialPool()
      jedis = pool.getResource
      local.set(jedis)
      println("Connected to " + jedis.getClient.getSocket.getRemoteSocketAddress)
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
    if (pool != null) {
      pool.close()
      pool = null
    }
  }

  private def initialPool(): Unit = {
    if (pool != null) return
    try {
      import collection.JavaConversions._
      val sentinels  =  mutable.Set[String](properties.getSentinels.split(",").toList:_*)
      pool = new JedisSentinelPool(properties.getMasterName, sentinels)
      println("Redis Sentinel线程池被成功初始化")
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
