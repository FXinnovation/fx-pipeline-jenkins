package com.fxinnovation.event_data

import com.fxinnovation.observer.EventDataInterface

class CheckoutData implements EventDataInterface {
  private String tag

  CheckoutData(String tag = '.') {
    this.tag = tag
  }

  String getTag() {
    return tag
  }

  void setTag(String tag) {
    this.tag = tag
  }
}
