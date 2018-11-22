package com.xmen.dc.http

import com.xmen.dc.reflect.ReflectHelper
import org.scalatest.FunSuite

class DaemonHttpServiceTest extends FunSuite {

  test("testAddHandler") {
    val func = ReflectHelper.getFunc[DaemonHttpService]("getHostIpAndName")
    println(func())
  }

}
