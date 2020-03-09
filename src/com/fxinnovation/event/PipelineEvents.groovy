package com.fxinnovation.event

class PipelineEvents {
  public final static PRE_PREPARE = 'prePrepare'
  public final static PREPARE = 'prepare'
  public final static POST_PREPARE = 'postPrepare'

  public final static PRE_BUILD = 'preBuild'
  public final static BUILD = 'build'
  public final static POST_BUILD = 'postBuild'

  public final static PRE_TEST = 'preTest'
  public final static TEST = 'test'
  public final static POST_TEST = 'postTest'

  public final static PRE_PUBLISH = 'prePublish'
  public final static PUBLISH = 'publish'
  public final static POST_PUBLISH = 'postPublish'
}
