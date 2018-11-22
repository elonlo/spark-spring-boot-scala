package com.xmen.dc.utils

import java.io.{BufferedReader, InputStreamReader}
import java.net.URI

import org.apache.commons.lang.StringUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import scala.collection.mutable.{HashSet, Set}

/**
  * Created by ElonLo on 1/29/2018.
  */
object DFSFileUtil {

  def writeContentsToFile(filePath: String, contents: String): Unit = {
    val conf = new Configuration
    val path = new Path(filePath)
    try {
      val fs = path.getFileSystem(conf)
      val outputStream = fs.create(path)
      outputStream.write(contents.getBytes)
      outputStream.flush
      outputStream.close
//      fs.close
    } catch {
      case e: Exception => e.printStackTrace
    }
  }

  def readContentsFromFile(filePath: String): Set[String] = {
    val conf = new Configuration
    val contents = new HashSet[String]
    try {
      val fs = FileSystem.get(URI.create(filePath), conf)
      val inputStream = fs.open(new Path(filePath))
      val bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))
      var line: String = ""
      do {
        if (!line.isEmpty)
          contents.add(line.trim)
        line = bufferedReader.readLine()
      } while (StringUtils.isNotEmpty(line))
    } catch {
      case e: Exception => e.printStackTrace
    }

    contents
  }

  /**
    * 删除DFS文件
    * @param filePath
    * @return
    */
  def delete(filePath: String): Boolean = {
    val conf = new Configuration
    val path = new Path(filePath)
    try {
      val fs = path.getFileSystem(conf)
      fs.delete(path, false)
    } catch {
      case e: Exception => e.printStackTrace
    }
    true
  }
}
