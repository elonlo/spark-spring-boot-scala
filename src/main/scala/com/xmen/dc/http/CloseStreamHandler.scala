package com.xmen.dc.http

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.log4j.LogManager
import org.apache.spark.streaming.StreamingContext
import org.spark_project.jetty.server.Request
import org.spark_project.jetty.server.handler.AbstractHandler

class CloseStreamHandler(ssc: StreamingContext) extends AbstractHandler{
  private val logger = LogManager.getLogger(classOf[CloseStreamHandler])

  override def handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): Unit = {
    logger.warn("开始关闭流任务......")
    ssc.stop(true, true)
    httpServletResponse.setContentType("text/html; charset=utf-8")
    httpServletResponse.setStatus(HttpServletResponse.SC_OK)
    val out = httpServletResponse.getWriter
    out.println("close success")
    request.setHandled(true)
    logger.warn("流任务关闭成功......")
  }
}
