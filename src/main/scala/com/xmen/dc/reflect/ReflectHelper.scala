package com.xmen.dc.reflect

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
  * 反射帮助类
  */
object ReflectHelper {
  /**
    * 通过反射获取函数
    * @param name
    * @tparam T
    * @return
    */
  def getFunc[T](name: String)(implicit tt : TypeTag[T],ct : ClassTag[T]): MethodMirror = {
    val mirror = runtimeMirror(ct.getClass.getClassLoader)
    val instance = ct.runtimeClass.newInstance().asInstanceOf[T]
    val rf = mirror.reflect(instance)
    val method = typeOf[T].decl(TermName(name)).asMethod
    rf.reflectMethod(method)
  }
}
