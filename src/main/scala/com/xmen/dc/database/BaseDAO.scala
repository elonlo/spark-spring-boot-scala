package com.xmen.dc.database

import java.sql.{Connection, ResultSet, SQLException, Statement}

import com.xmen.dc.manager.MysqlConnectManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

import scala.collection.mutable.ListBuffer
import scala.reflect._

@Repository
class BaseDAO {
  @Autowired
  private val connectManager: MysqlConnectManager = null

//  /**
//    * 查询返回Entity对象
//    * @param sql
//    * @return
//    */
//  def query[T: ClassTag](sql: String): List[T] = {
//    var ret: List[T] = null
//    var connection: Connection = null
//    var stmt: Statement = null
//    var rs: ResultSet = null
//    try {
//      connection = connectManager.getConnection()
//      stmt = connection.createStatement()
//      rs = stmt.executeQuery(sql)
//      val list: ListBuffer[T] = ResultSetMapper.listObjectFromResultSet[T](rs, classTag[T].runtimeClass)
//      if (list != null) ret = list.toList
//    } catch {
//      case e: SQLException => e.printStackTrace()
//    } finally {
//      connectManager.closeConnect(rs, stmt, connection)
//    }
//
//    ret
//  }

  /**
    * 查询返回Map
    * @param sql
    * @return
    */
  def query(sql: String): List[Map[String, String]] = {
    println(sql)
    var ret: List[Map[String, String]] = null
    var connection: Connection = null
    var stmt: Statement = null
    var rs: ResultSet = null
    try {
      connection = connectManager.getConnection()
      stmt = connection.createStatement()
      rs = stmt.executeQuery(sql)
      ret = ResultSetMapper.listObjectFromResultSet(rs)
    } catch {
      case e: SQLException => e.printStackTrace()
    } finally {
      connectManager.closeConnect(rs, stmt, connection)
    }

    ret
  }

  /**
    * 插入数据
    * @param sql
    * @return
    */
  def insert(sql: String): Boolean = {
    println(sql)
    var result = false
    var connection: Connection = null
    var stmt: Statement = null
    try {
      connection = connectManager.getConnection()
      stmt = connection.createStatement()
      val count = stmt.executeUpdate(sql)
      connection.commit()
      result = if (count > 0) true else false
    } catch {
      case e: SQLException => sys.error(e.toString)
    } finally {
      connectManager.closeConnect(null, stmt, connection)
    }
    result
  }
}
