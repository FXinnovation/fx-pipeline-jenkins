package com.fxinnovation.util

import com.cloudbees.groovy.cps.NonCPS

class FileUtils {
  @NonCPS
  public static exists(String filename) {
    return new File(filename).exists()
  }
}
