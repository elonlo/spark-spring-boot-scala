package com.xmen.dc.utils

import java.io.IOException

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path

import scala.collection.mutable.ListBuffer

/**
  * Created by ElonLo on 1/26/2018.
  */
object DFSDirectoryUtil {
  /**
    * 遍历输入目录获取所有数据分区
    * @param rootDir
    * @param f
    * @return
    */
  def getPartitionDirs(rootDir: String, f: String => Boolean): ListBuffer[String] = {
    val listBuffer = new ListBuffer[String]

    def listDirs(dir: String) {
      val conf = new Configuration
      try {
        val path = new Path(dir)
        val fs = path.getFileSystem(conf)
        val fileStatus = fs.listStatus(path)
        var dirCount = 0
        fileStatus.foreach(status => {
          if (status.isDirectory) {
            dirCount += 1
            listDirs(status.getPath.toString)
          }
        })
        if (dirCount <= 0) {
          if (f != null) {
            if (f(dir)) {
              listBuffer.append(dir)
            }
          } else {
            listBuffer.append(dir)
          }
        }
      } catch {
        case e: Exception => e.printStackTrace
      }
    }

    listDirs(rootDir)
    listBuffer
  }

  /**
    * 判断文件夹是否存在
    * @param fullPath
    * @return
    */
  def isExists(fullPath: String): Boolean = {
    var result = false
    val conf = new Configuration
    val path = new Path(fullPath)
    try {
      val fs = path.getFileSystem(conf)
      result = fs.isDirectory(path) && fs.exists(path)
    } catch {
      case e: Exception => e.printStackTrace
    }
    result
  }

  /**
    * 删除目录
    * @param fullPath
    * @return
    */
  def delete(fullPath: String): Boolean = {
    try {
      val conf = new Configuration
      val path = new Path(fullPath)
      val fileSystem = path.getFileSystem(conf)
      if (fileSystem.exists(path)) {
        return fileSystem.delete(path, true)
      }
    } catch {
      case e: IOException => e.printStackTrace
    }

    false
  }

  /**
    * 文件夹重命名
    * @param srcDir
    * @param destDir
    * @return
    */
  def rename(srcDir: String, destDir: String): Boolean = {
    try {
      val conf = new Configuration
      val srcPath = new Path(srcDir)
      val fileSystem = srcPath.getFileSystem(conf)
      if (fileSystem.exists(srcPath)) {
        return fileSystem.rename(srcPath, new Path(destDir))
      }
    } catch {
      case e: IOException => e.printStackTrace
    }

    false
  }
}
