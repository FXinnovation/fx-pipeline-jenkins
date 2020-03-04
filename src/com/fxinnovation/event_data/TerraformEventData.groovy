package com.fxinnovation.event_data

import com.fxinnovation.observer.EventDataInterface

class TerraformEventData implements EventDataInterface {
  private String commandTarget
  private Map<String,String> terraformOptions

  TerraformEventData(String commandTarget, Map<String, String> terraformOptions) {
    this.commandTarget = commandTarget
    this.terraformOptions = terraformOptions
  }

  List<String> getCommandTarget() {
    return commandTarget
  }

  Map<String, String> getTerraformOptions() {
    return terraformOptions
  }
}
