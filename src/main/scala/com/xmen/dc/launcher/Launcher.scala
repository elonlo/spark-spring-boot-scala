package com.xmen.dc.launcher

import com.xmen.dc.job.Job
import org.apache.commons.lang3.StringUtils
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.{ComponentScan, PropertySource}

@SpringBootApplication
@ComponentScan(basePackages = Array("com.xmen.dc"))
@PropertySource(Array("classpath:doMainApplication.properties", "classpath:config/application.properties"))
@EnableAutoConfiguration
class Launcher

object Launcher {
  /**
    * Spark程序运行主入口，一次只能运行一个Job
    * @param args
    */
  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      sys.error("参数错误, 参数如下:\n" +
        "参数1: 需要执行的任务的ClassName")
    } else {
      val t = args(0).toString
      if (StringUtils.isEmpty(t)) {
        sys.error("任务参数错误，请检查配置！")
      } else {
        val context: ConfigurableApplicationContext = new SpringApplicationBuilder(classOf[Launcher]).run(args: _*)
        println("Launcher start successfully")
        val mainClass = Class.forName(t)
        mainClass match {
          case j: Class[Job] => {
            val job = context.getBean(j)
            job.doTask()
          }
          case _ => sys.error("任务执行失败, 无法找到匹配的主类！")
        }
      }
    }
  }
}
