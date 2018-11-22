package com.xmen.dc.utils

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}
import com.xmen.dc.constant.TimeConsts
import org.apache.commons.lang3.time.FastDateFormat
import scala.collection.mutable.ListBuffer

/**
  * Created by ElonLo on 1/26/2018.
  */
object TimeUtil {
  private val dayFormat = new SimpleDateFormat(TimeConsts.DayPattern)
  private val wholeFormat = new SimpleDateFormat(TimeConsts.wholePattern)

  /**
    * 获取当天零时的时间戳
    * @return
    */
  def getZeroTimestamp(): Long = {
    val now = new Date
    dayFormat.parse(dayFormat.format(now)).getTime
  }

  /**
    * 获取相对于今日凌晨的时间的时间戳
    * @param ts 相对今天凌晨的时间
    * @return
    */
  def getTimestampFromTodayZero(ts: Long): Long = {
    getZeroTimestamp + ts
  }

  /**
    * 获取当前的日期
    * @return
    */
  def getNowDate(): String = {
    val now = new Date
    dayFormat.format(now)
  }

  /**
    * 根据毫秒时间戳获取日期
    * @param ts
    * @return
    */
  def getWholeData(ts: Long): String = {
    wholeFormat.format(new Date(ts))
  }

  /**
    * change ts to date
    * @param ts
    * @return
    */
  def getDateFromTimestamp(ts: Long): String = {
    dayFormat.format(ts)
  }

  /**
    * 获取昨天的日期
    * @return
    */
  def getYesterdayDate(): String = {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DATE, -1)
    dayFormat.format(cal.getTime)
  }

  /**
    * 查找两个时间区间内的所有日期
    * @param dBegin
    * @param dEnd
    * @return
    */
  def findDates(dBegin: Date, dEnd: Date): List[String] = {
    val lDate = new ListBuffer[String]
    lDate.append(dayFormat.format(dBegin))
    val calBegin = Calendar.getInstance
    // 使用给定的 Date 设置此 Calendar 的时间
    calBegin.setTime(dBegin)
    val calEnd = Calendar.getInstance
    calEnd.setTime(dEnd)
    // 测试此日期是否在指定日期之后
    while (dEnd.after(calBegin.getTime)) { // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
      calBegin.add(Calendar.DAY_OF_MONTH, 1)
      lDate.append(dayFormat.format(calBegin.getTime))
    }
    lDate.toList
  }
}
