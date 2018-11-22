package com.xmen.dc.reflect

import org.apache.log4j.LogManager
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

object MapMapper {
  private val logger = LogManager.getLogger(getClass)
  /**
    * 将类的域对应的值转换为Map返回
    * @return
    */
  def fieldAndValueToMap(outputClass: Class[_]) : Map[String, Any] = {
    (Map[String, Any]() /: outputClass.getDeclaredFields) {
      (a, f) => {
        f.setAccessible(true)
        a + (f.getName -> f.get(this))
      }
    }
  }

  /**
    * 将map数据反射为对象
    * @param map
    * @tparam T
    * @return
    */
  def mapReflectToObject[T](map: Map[String, String])(implicit tt : TypeTag[T],ct : ClassTag[T]): T = {
    val t = typeOf[T]
    val vars = t.members.filter(_ match {
      case t: TermSymbol => t.isVar
      case _ => false
    })

    val mirror = runtimeMirror(t.getClass.getClassLoader)
    val instance = ct.runtimeClass.newInstance().asInstanceOf[T]
    val rf = mirror.reflect(instance)
    vars.foreach {
      case t: TermSymbol => {
        map.get(t.name.decodedName.toString.trim) match {
          case Some(v) => {
            val setterMethod = rf.reflectMethod(t.setter.asMethod)
            t.getter.asMethod.returnType match {
              case ft if ft =:= typeOf[String] => setterMethod(v)
              case ft if ft =:= typeOf[Int] => setterMethod(v.toInt)
              case ft if ft =:= typeOf[Long] => setterMethod(v.toLong)
              case ft if ft =:= typeOf[Boolean] => setterMethod(v.toBoolean)
              case ft if ft =:= typeOf[Double] => setterMethod(v.toDouble)
              case ft if ft =:= typeOf[Float] => setterMethod(v.toFloat)
              case ut => logger.warn("Unknown type: " + ut.toString)
            }
          }
          case None => logger.debug("Value not found: " + t.name.decodedName.toString)
        }
      }
    }
    instance
  }
}
