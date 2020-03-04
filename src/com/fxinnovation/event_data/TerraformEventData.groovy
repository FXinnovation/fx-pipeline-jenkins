package com.fxinnovation.event_data

import com.fxinnovation.observer.EventDataInterface

class TerraformEventData implements EventDataInterface {
  private String commandTarget
  private Map<String,String> options

  TerraformEventData(String commandTarget, Map<String, String> options) {
    this.commandTarget = commandTarget
    this.options = options
  }

  List<String> getCommandTarget() {
    return commandTarget
  }

  Map<String, String> getOptions() {
    return options
  }
}
