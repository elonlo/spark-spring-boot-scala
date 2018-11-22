package com.xmen.dc.manager

import java.sql.{Connection, ResultSet, Statement}

import com.xmen.dc.properties.MysqlPoolProperties
import org.apache.commons.dbcp2.BasicDataSource

/**
  * MYSQL连接管理
  * @param properties
  */
class MysqlConnectManager(properties: MysqlPoolProperties) extends Serializable {
  private var bs: BasicDataSource = _

  private def getDataSource(): BasicDataSource = {
    if (bs == null) {
      bs = new BasicDataSource
      bs.setDriverClassName(properties.getDriverClassName)
      bs.setUrl(properties.getUrl)
      bs.setUsername(properties.getUsername)
      bs.setPassword(properties.getPassword)
      bs.setMaxTotal(200) //设置最大并发数
      bs.setInitialSize(properties.getInitSize) //数据库初始化时，创建的连接个数
      bs.setMinIdle(20) //最小空闲连接数
      bs.setMaxIdle(200) //数据库最大连接数
      bs.setMaxWaitMillis(1000)
      bs.setMinEvictableIdleTimeMillis(60 * 1000) //空闲连接60秒中后释放
      bs.setTimeBetweenEvictionRunsMillis(5*60*1000) //5分钟检测一次是否有死掉的线程
      bs.setTestOnBorrow(true)
    }
    bs
  }

  def shutdownDataSource() = {
    if (bs != null) {
      bs.close()
    }
  }

  def getConnection(): Connection = {
    var connection: Connection = null
    try {
      if (bs != null) {
        connection = bs.getConnection
      } else {
        connection = getDataSource().getConnection
      }
      connection.setAutoCommit(false)
    } catch {
      case e: Exception => {
        sys.error(e.getMessage)
      }
    }

    connection
  }

  def closeConnect(rs: ResultSet, st: Statement, connection: Connection): Unit = {
    if (rs != null) {
      try {
        rs.close
      } catch {
        case e: Exception => sys.error("关闭结果集ResultSet异常！" + e.getMessage())
      }
    }

    if (st != null) {
      try {
        st.close
      } catch {
        case e: Exception =>
          sys.error("Statement SQL语句关闭异常！" + e.getMessage)
      }
    }

    if (connection != null) {
      try {
        connection.close
      } catch {
        case e: Exception =>
          sys.error("关闭连接对象Connection异常！" + e.getMessage)
      }
    }
  }
}
