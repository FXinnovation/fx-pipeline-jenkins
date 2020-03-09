package com.fxinnovation.util

import com.cloudbees.groovy.cps.NonCPS

class FileUtils {
  @NonCPS
  static exists(String filename) {
    return new File(filename).exists()
  }
}
