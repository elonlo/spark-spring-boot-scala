package com.xmen.dc.utils

import java.security.MessageDigest

/**
  * Created by ElonLo on 1/26/2018.
  */
object SecurityUtil {
  def sha1(s: String): String = {
    val md = MessageDigest.getInstance("SHA-1")
    md.digest(s.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }
}
