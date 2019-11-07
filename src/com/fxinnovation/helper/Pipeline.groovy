package com.fxinnovation.helper

import com.fxinnovation.helper.GitBranch

class Pipeline {
  /**
   * Check whether or not a branch is publishable
   * @param Map scmInfo
   **/
  static boolean isPublishable(Map scmInfo) {
    return(
      GitBranch.isTagged(scmInfo) &&
      (
        GitBranch.hasCompliantMasterTag(scmInfo) ||
        GitBranch.hasCompliantDevTag(scmInfo)
      )
    );
  }
}
