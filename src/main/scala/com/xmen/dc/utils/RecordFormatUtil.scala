package com.xmen.dc.utils

import com.xmen.dc.constant.FileConsts
import scala.collection.mutable

object RecordFormatUtil {

  /**
    * 将record的key和value转换成map
    * @param keys
    * @param values
    * @return
    */
  def recordToMap(keys: Array[String], values: Array[String]): Map[String, String] = {
    val keyValueMap = mutable.Map[String, String]()
    (keys, values).zipped.foreach((x, y) => {
      keyValueMap(x) = if (y.trim.length == 0) "" else y
    })
    keyValueMap.toMap
  }

  def defaultRecordToMap(meta: String, values: Array[String]): Map[String, String] = {
    recordToMap(meta.split(FileConsts.VERTICAL_LINE_SPLIT_REGEX), values)
  }

  def defaultRecordToMap(meta: String, values: String): Map[String, String] = {
    recordToMap(meta.split(FileConsts.VERTICAL_LINE_SPLIT_REGEX), values.split(FileConsts.VERTICAL_LINE_SPLIT_REGEX))
  }
}
