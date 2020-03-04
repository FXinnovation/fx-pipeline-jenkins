package com.fxinnovation.event_data

import com.fxinnovation.observer.EventDataInterface

class TerraformEventData implements EventDataInterface {
  private List<String> commandTargets
  private Map<String,String> options

  TerraformEventData(List<String> commandTargets, Map<String, String> options) {
    this.commandTargets = commandTargets
    this.options = options
  }

  List<String> getCommandTargets() {
    return this.commandTargets
  }

  Map<String, String> getOptions() {
    return this.options
  }
}
