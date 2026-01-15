package com.shtoone.shtoone_push.constants;

public enum Channel {
  TEST("test_event"),
  TOKEN("token_event"),
  NOTIFICATION_CLICK("notification_click"),
  NOTIFICATION_ARRIVED("notification_arrived"),

  ERROR("error");


  private final String channelKey;

  Channel(String channelKey) {
    this.channelKey = channelKey;
  }

  public String key() {
    return channelKey;
  }
}

