package com.xmen.dc.http

import java.net.InetAddress
import com.xmen.dc.database.BaseDAO
import com.xmen.dc.properties.JobProperties
import org.apache.spark.streaming.StreamingContext
import org.spark_project.jetty.server.Server
import org.spark_project.jetty.server.handler.{AbstractHandler, ContextHandler, ContextHandlerCollection}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

@Service
class DaemonHttpService extends BaseDAO{
  @Autowired
  private val jobProperties: JobProperties = null
  @Autowired
  private val ssc: StreamingContext = null

  private val handlerMap = mutable.Map[String, AbstractHandler]()

  def start(isAddCloseHandler: Boolean = true): Unit = {
    println("正在启动进程守护服务......")
    val port = jobProperties.getPort
    if (port == null || port <= 1024) {
      sys.error("进程守护服务端口不合法，请检查是否设置或是否大于1024！")
      return
    }
    if (isAddCloseHandler) addCloseHandler(ssc)

    val server = new Server(port)
    val contexts = new ContextHandlerCollection()
    val contextList = ListBuffer[ContextHandler]()
    val operations = new ListBuffer[String]()
    for (handler <- handlerMap) {
      val context = new ContextHandler()
      context.setContextPath(s"/${handler._1}")
      context.setHandler(handler._2)
      contextList.append(context)
      operations.append(handler._1)
    }
    contexts.setHandlers(contextList.toArray)
    server.setHandler(contexts)
    server.start()

    if (server.isRunning) {
      saveDaemonServerAddress()
      setDaemonServerOperation(operations.mkString("|"))
      setDaemonServerStatus(true)

      scala.sys.addShutdownHook({
        println("正在关闭进程守护服务......")
        setDaemonServerStatus(false)
        server.stop()
        println("关闭进程守护服务成功......")
      })
      println("启动进程守护服务完毕......")
    } else {
      println("启动进程守护服务失败......")
    }
  }

  /**
    * 添加http操作
    * @param name
    * @param handler
    */
  def addHandler(name: String, handler: AbstractHandler): Unit = {
    handlerMap.put(name, handler)
  }

  /**
    * 添加优雅关闭操作
    * @param ssc
    */
  private def addCloseHandler(ssc: StreamingContext): Unit = {
    addHandler("close", new CloseStreamHandler(ssc))
  }

  /**
    * 保存守护进程的访问地址
    */
  private def saveDaemonServerAddress(): Unit = {
    val ipAndHostName = getHostIpAndName()
    val port = jobProperties.getPort
    val id = jobProperties.getName
    val ipAddress = s"http://${ipAndHostName._1}:$port/"
    val domainAddress = s"http://${ipAndHostName._2}:$port/"

    val sql = s"replace into spark.http_daemon_server_info(id, ip_address, domain_address, update_time) " +
      s"values('$id', '$ipAddress', '$domainAddress', now())"
    insert(sql)
  }

  /**
    * 设置守护进程的状态
    * @param status
    */
  private def setDaemonServerStatus(status: Boolean) = {
    val id = jobProperties.getName
    val sql = s"update spark.http_daemon_server_info set status = ${if (status) 1 else 0}, update_time = now() where id = '$id'"
    insert(sql)
  }

  private def setDaemonServerOperation(operations: String) = {
    val id = jobProperties.getName
    val sql = s"update spark.http_daemon_server_info set operation = '$operations', update_time = now() where id = '$id'"
    insert(sql)
  }

  /**
    * 获取宿主机的ip(内网)和name
    * @return
    */
  private def getHostIpAndName(): (String, String) = {
    val host = InetAddress.getLocalHost
    val ip = host.getHostAddress
    val hostname = host.getHostName
    (ip, hostname)
  }
}
