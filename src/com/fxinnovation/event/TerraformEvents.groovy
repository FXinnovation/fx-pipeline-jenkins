package com.fxinnovation.event

class TerraformEvents {
  public final static PRE_INIT = 'terraformPreInit'
  public final static INIT = 'terraformInit'
  public final static POST_INIT = 'terraformPostInit'

  public final static PRE_FMT = 'terraformPreFmt'
  public final static FMT = 'terraformFmt'
  public final static POST_FMT = 'terraformPostFmt'

  public final static PRE_VALIDATE = 'terraformPreValidate'
  public final static VALIDATE = 'terraformValidate'
  public final static POST_VALIDATE = 'terraformPostValidate'

  public final static PRE_PLAN = 'terraformPrePlan'
  public final static PLAN = 'terraformPlan'
  public final static POST_PLAN = 'terraformPostPlan'

  public final static PRE_PLAN_REPLAY = 'terraformPrePlanReplay'
  public final static PLAN_REPLAY = 'terraformPlanReplay'
  public final static POST_PLAN_REPLAY = 'terraformPostPlanReplay'

  public final static PRE_APPLY = 'terraformPreApply'
  public final static APPLY = 'terraformApply'
  public final static POST_APPLY = 'terraformPostApply'

  public final static PRE_DESTROY = 'terraformPreDestroy'
  public final static DESTROY = 'terraformDestroy'
  public final static POST_DESTROY = 'terraformPostDestroy'

  public final static PRE_PIPELINE = 'terraformPrePipeline'
  public final static POST_PIPELINE = 'terraformPostPipeline'
}
