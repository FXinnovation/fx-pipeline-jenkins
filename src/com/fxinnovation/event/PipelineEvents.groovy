package com.fxinnovation.event

class PipelineEvents {
  public final PRE_PREPARE = 'prePrepare'
  public final PREPARE = 'prepare'
  public final POST_PREPARE = 'postPrepare'

  public final PRE_BUILD = 'preBuild'
  public final BUILD = 'build'
  public final POST_BUILD = 'postBuild'

  public final PRE_TEST = 'preTest'
  public final TEST = 'test'
  public final POST_TEST = 'postTest'

  public final PRE_PUBLISH = 'prePublish'
  public final PUBLISH = 'publish'
  public final POST_PUBLISH = 'postPublish'
}
