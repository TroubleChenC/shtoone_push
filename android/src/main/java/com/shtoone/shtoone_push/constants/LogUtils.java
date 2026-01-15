package com.shtoone.shtoone_push.constants;

import io.flutter.Log;

public class LogUtils {
  public static String tag = "shtoone_push";

  public static void d(String msg) {
    Log.d(tag, msg);
  }

  public static void e(String msg) {
    Log.e(tag, msg);
  }
}
