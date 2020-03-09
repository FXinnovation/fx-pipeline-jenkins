package com.fxinnovation.event_data

import com.fxinnovation.data.ScmInfo
import com.fxinnovation.di.IOC
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventDispatcher

class TerraformEventData implements EventDataInterface {
  private String commandTarget
  private ScmInfo scmInfo = IOC.get(EventDispatcher.class.getName())
  private Map<String,String> extraOptions
  private String stateFileName
  private String testStateFileName
  private String planOutFile
  private Map<String,String> extraData

  TerraformEventData(String commandTarget = '.', String stateFileName = 'state.tfstate', String testStateFileName = 'test.tfstate', String planOutFile = 'out.plan') {
    this.commandTarget = commandTarget
    this.stateFileName = stateFileName
    this.testStateFileName = testStateFileName
    this.planOutFile = planOutFile
  }

  List<String> getCommandTarget() {
    return commandTarget
  }

  ScmInfo getScmInfo() {
    return scmInfo
  }

  Map<String, String> getExtraOptions() {
    return extraOptions
  }

  String getStateFileName() {
    return stateFileName
  }

  String getTestStateFileName() {
    return testStateFileName
  }

  String getPlanOutFile() {
    return planOutFile
  }

  Map<String, String> getExtraData() {
    return extraData
  }

  TerraformEventData setExtraOptions(Map<String, String> extraOptions = [:]) {
    this.extraOptions = null != extraOptions ? extraOptions : [:]
    return this
  }

  TerraformEventData setExtraData(Map<String, String> extraData) {
    this.extraData = null != extraData ? extraData : [:]
    return this
  }
}
