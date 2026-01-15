package com.shtoone.shtoone_push;

import android.os.Handler;
import android.os.Looper;

import com.shtoone.shtoone_push.constants.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.flutter.plugin.common.EventChannel;

public class PushEventDispatcher {
  private static PushNotification initPushNotification;

  public static void setInitPushNotification(PushNotification message) {
    initPushNotification = message;
  }

  public static PushNotification getInitNotification() {
    PushNotification tmp = initPushNotification;
//    initPushNotification = null;
    return tmp;
  }

  private static final Map<Channel, EventChannel.EventSink> sinks =
      new ConcurrentHashMap<>();

  public static void bind(Channel type, EventChannel.EventSink sink) {
    sinks.put(type, sink);
  }

  public static void unbind(Channel type) {
    sinks.remove(type);
  }

  public static void send(Channel type, Object data) {
    EventChannel.EventSink sink = sinks.get(type);
    if (sink == null) return;

    if (Looper.myLooper() == Looper.getMainLooper()) {
      sink.success(data);
    } else {
      new Handler(Looper.getMainLooper()).post(() -> {
        sink.success(data);
      });
    }
  }

  public static void sendError(Channel type, String code, String msg, String detail) {
    EventChannel.EventSink sink = sinks.get(type);
    if (sink == null) return;

    if (Looper.myLooper() == Looper.getMainLooper()) {
      sink.error(code, msg, detail);
    } else {
      new Handler(Looper.getMainLooper()).post(() -> {
        sink.error(code, msg, detail);
      });
    }
  }

}


