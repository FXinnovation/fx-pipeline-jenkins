package com.fxinnovation.data

class ScmInfo implements Serializable {
  public final SEMVER_REGEXP = /^(?<FullPatch>(?<FullMinor>(?<Major>0|[1-9]\d*)\.(?<Minor>0|[1-9]\d*))\.(?<Patch>0|[1-9]\d*))(?<PreReleaseTagWithSeparator>-(?<PreReleaseTag>([a-z-][\da-z-]+|[\da-z-]+[a-z-][\da-z-]*|0|[1-9]\d*)(\.([a-z-][\da-z-]+|[\da-z-]+[a-z-][\da-z-]*|0|[1-9]\d*))*))?(?<BuildMetadataWithSeparator>\+(?<BuildMetadata>[\da-z-]+(\.[\da-z-]+)*))?$/

  private String commitId;
  private String lastCommitId;
  private String branch;
  private String defaultBranch;
  private String tag;
  private String latestTag;
  private String repositoryName;

  ScmInfo(String commitId, String lastCommitId, String branch, String defaultBranch, String tag, String latestTag, String repositoryName) {
    this.commitId = commitId
    this.lastCommitId = lastCommitId
    this.branch = branch
    this.defaultBranch = defaultBranch
    this.tag = tag
    this.latestTag = latestTag
    this.repositoryName = repositoryName
  }

  String toString() {
    return (
      "\nRepository: "+ this.getRepositoryName() +
      "\nCommit: "+ this.getCommitId() +
      "\nLatest commit: "+ this.getLastCommitId() +
      "\nCommit is latest: "+ this.isCurrentCommitLatest() +
      "\nBranch: "+ this.getBranch() +
      "\nDefault branch: "+ this.getDefaultBranch() +
      "\nTag: "+ this.getTag() +
      "\nPre release tag: "+ this.getPreReleaseTag() +
      "\nLatest tag: "+ this.getLatestTag() +
      "\nTag is semver: "+ this.hasSemverTag() +
      "\nTag is latest: "+ this.isCurrentTagLatest() +
      "\nIs a pull request: "+ this.isPullRequest() +
      "\nIs publishable: "+ this.isPublishable() +
      "\nIs publishable as DEV version: "+ this.isPublishableAsDev()
    )
  }

  /**
   * Checks whether or not the current checkout is a pull request or not
   */
  Boolean isPullRequest() {
    return this.branch ==~ /^PR-[0-9]*$/
  }

  /**
   * Get the full tag (MAJOR.MINOR.PATCH) without the pre-release.
   */
  String getPatchTag() {
    if (!this.hasSemverTag()) {
      return ''
    }

    return (this.tag =~ this.SEMVER_REGEXP)[0][1] instanceof CharSequence ? (this.tag =~ this.SEMVER_REGEXP)[0][1] : ''
  }

  /**
   * Get only the PATCH digit of the current release.
   */
  Integer getPatchVersionNumber() {
    return  (this.tag =~ this.SEMVER_REGEXP) ? (this.tag =~ this.SEMVER_REGEXP)[0][5].toInteger() : null
  }

  /**
   * Get the minor tag (MAJOR.MINOR) without patch and the pre-release.
   */
  String getMinorTag() {
    if (!this.hasSemverTag()) {
      return ''
    }

    return (this.tag =~ this.SEMVER_REGEXP)[0][2] instanceof CharSequence ? (this.tag =~ this.SEMVER_REGEXP)[0][2] : ''
  }

  /**
   * Get only the MINOR digit of the current release.
   */
  Integer getMinorVersionNumber() {
    return  (this.tag =~ this.SEMVER_REGEXP) ? (this.tag =~ this.SEMVER_REGEXP)[0][4].toInteger() : null
  }

  /**
   * Get the major tag (MAJOR) without minor, patch and the pre-release.
   */
  String getMajorTag() {
    if (!this.hasSemverTag()) {
      return ''
    }

    return (this.tag =~ this.SEMVER_REGEXP)[0][3] instanceof CharSequence ? (this.tag =~ this.SEMVER_REGEXP)[0][3] : ''
  }

  /**
   * Get the pre-release tag (PRE-RELEASE) without major, minor and patch.
   */
  String getPreReleaseTag() {
    if (!this.hasSemverTag()) {
      return ''
    }

    return (this.tag =~ this.SEMVER_REGEXP)[0][7] instanceof CharSequence ? (this.tag =~ this.SEMVER_REGEXP)[0][7] : ''
  }

  /**
   * Get only the MAJOR digit of the current release. Equivalent to getMajorTag(), except this return an Integer.
   */
  Integer getMajorVersionNumber() {
    return  (this.tag =~ this.SEMVER_REGEXP) ? (this.tag =~ this.SEMVER_REGEXP)[0][3].toInteger() : null
  }

  /**
   * Get the current branch formatted to be a valid docker tag
   */
  String getBranchAsDockerTag() {
    return this.getBranch().replace('/','_')
  }

  /**
   * Checks whether or not the current branch has a tag following SEMVER standard.
   **/
  Boolean hasSemverTag() {
    return this.tag ==~ this.SEMVER_REGEXP
  }

  /**
   * Checks whether or not the current tag is the latest tag
   */
  Boolean isCurrentTagLatest() {
    return (
      this.latestTag == this.tag
    )
  }

  /**
   * Checks whether or not the current commit is the latest commit
   */
  Boolean isCurrentCommitLatest() {
    return (
      this.commitId == this.lastCommitId
    )
  }

  /**
   * Checks whether release is tagged or not.
   **/
  Boolean isTagged() {
    return '' != this.tag
  }

  /**
   * Whether or not the current commit is publishable as a tagged version, dev version or anything
   **/
  Boolean isPublishableAsAnything() {
    return (
      this.isPublishable() ||
      this.isPublishableAsDev()
    )
  }

  /**
   * Whether or not the current commit is publishable as a tagged version
   **/
  Boolean isPublishable() {
    return (
      this.defaultBranch == this.branch &&
      this.isCurrentCommitLatest() &&
      this.isTagged() &&
      this.hasSemverTag() &&
      '' == this.getPreReleaseTag() &&
      this.isCurrentTagLatest()
    )
  }

  /**
   * Whether or not the current commit is publishable as a development/testing version
   **/
  Boolean isPublishableAsDev() {
    return (
      this.isTagged() &&
      this.hasSemverTag() &&
      '' != this.getPreReleaseTag() &&
      this.tag =~ /-dev[\d]*$/
    )
  }

  String getCommitId() {
    return commitId
  }

  String getLastCommitId() {
    return lastCommitId
  }

  String getBranch() {
    return branch
  }

  String getDefaultBranch() {
    return defaultBranch
  }

  String getTag() {
    return tag
  }

  String getLatestTag() {
    return latestTag
  }

  String getRepositoryName() {
    return repositoryName
  }
}
