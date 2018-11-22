package com.xmen.dc.database

import java.lang.reflect.InvocationTargetException
import java.sql.ResultSet
import java.sql.SQLException
import javax.persistence.Column
import javax.persistence.Entity
import org.apache.commons.beanutils.BeanUtils
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import util.control.Breaks._

object ResultSetMapper {

//  /**
//    * 将ResultSet转换成自定义Entity对象
//    * @param rs
//    * @param outputClass
//    * @tparam T
//    * @return
//    */
//  @SuppressWarnings(Array("unchecked"))
//  def listObjectFromResultSet[T](rs: ResultSet, outputClass: Class[_]): ListBuffer[T] = {
//    var outputList: ListBuffer[T] = null
//    try {
//      // make sure resultset is not null
//      if (rs != null) {
//        // check if outputClass has 'Entity' annotation
//        if (outputClass.isAnnotationPresent(classOf[Entity])) {
//          // get the resultset metadata
//          val rsmd = rs.getMetaData
//          // get all the attributes of outputClass
//          val fields = outputClass.getDeclaredFields
//          while (rs.next) {
//            val bean = outputClass.newInstance.asInstanceOf[T]
//            var _iterator = 0
//            while (_iterator < rsmd.getColumnCount) {
//              // getting the SQL column name
//              val columnName = rsmd.getColumnName(_iterator + 1)
//              // reading the value of the SQL column
//              val columnValue = rs.getObject(_iterator + 1)
//              // iterating over outputClass attributes to check if
//              // any attribute has 'Column' annotation with
//              // matching 'name' value
//              breakable(
//                for (field <- fields) {
//                  if (field.isAnnotationPresent(classOf[Column])) {
//                    val column = field.getAnnotation(classOf[Column])
//                    if (column.name.equalsIgnoreCase(columnName) && columnValue != null) {
//                      BeanUtils.setProperty(bean, field.getName, columnValue)
//                      break
//                    }
//                  }
//                }
//              )
//              _iterator += 1
//            }
//            if (outputList == null) {
//              outputList = new ListBuffer[T]
//            }
//
//            outputList.append(bean)
//          }
//        } else {
//          sys.error("Output class don't exists 'Entity' annotation, please check.")
//        }
//      }
//    } catch {
//      case e: IllegalAccessException => e.printStackTrace()
//      case e: SQLException => e.printStackTrace()
//      case e: InstantiationException => e.printStackTrace()
//      case e: InvocationTargetException => e.printStackTrace()
//    }
//    outputList
//  }

  /**
    * 对象ResultSet转化成Map
    * @param rs
    * @return
    */
  def listObjectFromResultSet(rs: ResultSet): List[Map[String, String]] = {
    val outputList: ListBuffer[Map[String, String]] = new ListBuffer[Map[String, String]]
    while (rs.next) {
      val map: mutable.Map[String, String] = mutable.Map()
      val rsmd = rs.getMetaData
      val count = rsmd.getColumnCount
      for (i <- 1 to count) {
        val key = rsmd.getColumnLabel(i)
        val value = rs.getString(i)
        map.put(key, value)
      }
      outputList.append(map.toMap)
    }
    outputList.toList
  }
}

