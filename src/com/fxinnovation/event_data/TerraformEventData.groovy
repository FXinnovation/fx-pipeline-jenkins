package com.fxinnovation.event_data

import com.fxinnovation.observer.EventDataInterface

class TerraformEventData implements EventDataInterface {
  private String commandTarget
  private Map<String,String> terraformOptions
  private String stateFileName
  private String testStateFileName
  private String planOutFile

  TerraformEventData(String commandTarget = '.', Map<String, String> terraformOptions = [:], String stateFileName = '', String testStateFileName = '', String planOutFile = '') {
    this.commandTarget = commandTarget
    this.terraformOptions = terraformOptions
    this.stateFileName = stateFileName
    this.testStateFileName = testStateFileName
    this.planOutFile = planOutFile
  }

  List<String> getCommandTarget() {
    return commandTarget
  }

  Map<String, String> getTerraformOptions() {
    return terraformOptions
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

  void setTerraformOptions(Map<String, String> terraformOptions) {
    this.terraformOptions = terraformOptions
  }
}
