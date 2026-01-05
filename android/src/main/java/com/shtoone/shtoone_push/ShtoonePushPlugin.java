package com.shtoone.shtoone_push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** ShtoonePushPlugin */
public class ShtoonePushPlugin implements FlutterPlugin, MethodCallHandler {
  private MethodChannel channel;


  private EventChannel tokenEventChannel; // 收到注册regId事件

  private EventChannel testEventChannel; // 测试

  private EventChannel notificationClickChannel; // 点击通知消息事件
  private EventChannel notificationArrivedChannel; // 消息到达事件

  private BroadcastReceiver pushReceiver;

  private Context context;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "shtoone_push");
    channel.setMethodCallHandler(this);

    this.context = flutterPluginBinding.getApplicationContext();

    registerPushReceiver(context);

    setStreamHandlers(flutterPluginBinding.getBinaryMessenger());
  }

  // 设置事件监听
  private void setStreamHandlers(BinaryMessenger messenger) {
    // 获取token event
    tokenEventChannel = new EventChannel(messenger, Channel.TOKEN.key());
    tokenEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object o, EventChannel.EventSink eventSink) {
        PushEventDispatcher.bind(Channel.TOKEN, eventSink);
        String token = MiPushClient.getRegId(context);
        if (token != null && !token.isEmpty()) {
          eventSink.success(token);
        }
      }

      @Override
      public void onCancel(Object o) {
      }
    });

    // 点击通知事件
    notificationClickChannel = new EventChannel(messenger, Channel.NOTIFICATION_CLICK.key());
    notificationClickChannel.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object o, EventChannel.EventSink eventSink) {
        PushEventDispatcher.bind(Channel.NOTIFICATION_CLICK, eventSink);
      }

      @Override
      public void onCancel(Object o) {
      }
    });

    // 消息到达事件
    notificationArrivedChannel = new EventChannel(messenger, Channel.NOTIFICATION_ARRIVED.key());
    notificationArrivedChannel.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object o, EventChannel.EventSink eventSink) {
        PushEventDispatcher.bind(Channel.NOTIFICATION_ARRIVED, eventSink);
      }

      @Override
      public void onCancel(Object o) {

      }
    });

    // 测试
    testEventChannel = new EventChannel(messenger, Channel.TEST.key());
    testEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object o, EventChannel.EventSink eventSink) {
        PushEventDispatcher.bind(Channel.TEST, eventSink);
        PushEventDispatcher.send(Channel.TEST, "first listen");
      }

      @Override
      public void onCancel(Object o) {

      }
    });
  }

  // 注册推送消息接受 消息接受可能在新进程，所以需要通过intent传递
  private void registerPushReceiver(Context context) {
    if (pushReceiver != null) return;

    try {
      pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          String json = intent.getStringExtra("data");
          String type = intent.getStringExtra("type");

          try {
            Channel channel = Channel.valueOf(type);
            PushEventDispatcher.send(channel, json);

            // 如果是点击通知消息打开的app，缓存下这个消息，方便app打开后可以获取到唤醒自己的那条消息
            if (channel == Channel.NOTIFICATION_CLICK) {
              PushEventDispatcher.cacheClick(json);
            }
          } catch (IllegalArgumentException e) {
            Log.e("shtoone_push", "unknown intent type : " + type);
          }
        }
      };

      IntentFilter filter = new IntentFilter(PushIntent.ACTION_PUSH_EVENT);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.registerReceiver(
            pushReceiver,
            filter,
            Context.RECEIVER_EXPORTED
        );
      } else {
        ContextCompat.registerReceiver(context, pushReceiver, filter, ContextCompat.RECEIVER_EXPORTED);
      }
    } catch (Exception e) {
      Log.e("shtoone_push", e.toString());
    }

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (Method.valueOf(call.method)) {
      case getToken:
        String appId = call.argument("appId");
        String appKey = call.argument("appKey");
        if (appId == null || appKey == null) {
          Log.e("shtoone_push", "getToken: no param appId or appKey");
          return;
        }
        MiPushClient.registerPush(context, appId, appKey);
        break;
      case emit: // 测试事件发送
        PushEventDispatcher.send(Channel.TEST, "dispatcher event");
        PushEventDispatcher.sendError(Channel.TEST, "-1", "error test", "error detail");

        Map<String, Object> obj = new HashMap<>();
        obj.put("name", "chen");
        obj.put("age", 18);
        obj.put("gender", "male");
        PushEventDispatcher.send(Channel.NOTIFICATION_CLICK, new JSONObject(obj).toString());
        break;
      case getInitialNotification: // 获取点击通知打开app的消息
        String msg = PushEventDispatcher.consumeCachedClick();
        result.success(msg);
        break;
      case getBrand:
        result.success(Build.BRAND);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    tokenEventChannel.setStreamHandler(null);
    testEventChannel.setStreamHandler(null);
    notificationClickChannel.setStreamHandler(null);
    notificationArrivedChannel.setStreamHandler(null);
  }
}
