package com.shtoone.shtoone_push;

import android.content.Context;
import android.content.Intent;

public final class PushIntent {

  public static final String ACTION_PUSH_EVENT =
      "com.shtoone.shtoone_push.PUSH_EVENT";

  public static void notifyMainProcess(Context context, String json, Channel type) {
    Intent intent = new Intent(ACTION_PUSH_EVENT);
    intent.setPackage(context.getPackageName());
    intent.putExtra("data", json);
    intent.putExtra("type", type.name());
    context.sendBroadcast(intent);
  }
}

