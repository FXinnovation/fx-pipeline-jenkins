package com.fxinnovation.helper

class GitBranch {
  /**
   * Checks that branch is tagged or not
   * @param Map scmInfo
   **/
  static boolean isTagged(Map scmInfo) {
    return '' != scmInfo.tag
  }

  /**
   * Checks that the tag is on master and FX compliant
   * @param Map scmInfo
   **/
  static boolean hasCompliantMasterTag(Map scmInfo) {
    return(
      'master' == scmInfo.branch &&
        scmInfo.tag =~ /^(([0-9]|[1-9][0-9]+)\.+){2}([0-9]|[1-9][0-9]+)$/
    );
  }

  /**
   * Checks that the tag not on master and FX compliant for development
   * @param Map scmInfo
   **/
  static boolean hasCompliantDevTag(Map scmInfo) {
    return(
      'master' != scmInfo.branch &&
        scmInfo.tag =~ /^(([0-9]|[1-9][0-9]+)\.+){2}([0-9]|[1-9][0-9]+)-dev.*$/
    );
  }
}
