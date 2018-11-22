package com.xmen.dc.filter

import java.io.IOException
import com.xmen.dc.constant.FileConsts
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{Path, PathFilter}

/**
  * Created by ElonLo on 1/9/2018.
  */
class TempFileInputFilter extends PathFilter{
  override def accept(path: Path): Boolean = {
    try {
      val fs = path.getFileSystem(new Configuration)
      if (fs.isDirectory(path)) return true
      return !path.getName.endsWith(FileConsts.TMP)
    } catch {
      case e: IOException => e.printStackTrace()
    }
    false
  }
}
