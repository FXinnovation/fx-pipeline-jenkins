package com.fxinnovation.event_data

import com.fxinnovation.observer.EventDataInterface

class TerraformEventData implements EventDataInterface {
  private String commandTarget
  private Map<String,String> extraOptions
  private String stateFileName
  private String testStateFileName
  private String planOutFile

  TerraformEventData(String commandTarget = '.', Map<String, String> extraOptions = [:], String stateFileName = '', String testStateFileName = '', String planOutFile = '') {
    this.commandTarget = commandTarget
    this.extraOptions = null != extraOptions ? extraOptions : [:]
    this.stateFileName = stateFileName
    this.testStateFileName = testStateFileName
    this.planOutFile = planOutFile
  }

  List<String> getCommandTarget() {
    return commandTarget
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

  void setExtraOptions(Map<String, String> extraOptions = [:]) {
    this.extraOptions = null != extraOptions ? extraOptions : [:]
  }
}
