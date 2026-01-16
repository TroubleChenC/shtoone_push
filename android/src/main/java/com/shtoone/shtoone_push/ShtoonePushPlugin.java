package com.shtoone.shtoone_push;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.shtoone.shtoone_push.Utils.HwUtils;
import com.shtoone.shtoone_push.constants.Channel;
import com.shtoone.shtoone_push.constants.LogUtils;
import com.shtoone.shtoone_push.constants.Method;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.HashMap;
import java.util.Map;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * ShtoonePushPlugin
 */
public class ShtoonePushPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  private MethodChannel channel;

  private EventChannel testEventChannel; // 测试

  private EventChannel tokenEventChannel; // 收到注册regId事件
  private EventChannel notificationClickChannel; // 点击通知消息事件
  private EventChannel notificationArrivedChannel; // 消息到达事件

  private BroadcastReceiver pushReceiver;

  private Context context;

  private Activity activity;

  private NotificationIntentListener notificationIntentListener;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), LogUtils.tag);
    channel.setMethodCallHandler(this);

    this.context = flutterPluginBinding.getApplicationContext();

    notificationIntentListener = new NotificationIntentListener();

    registerPushReceiver(context);

    setStreamHandlers(flutterPluginBinding.getBinaryMessenger());
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    handleClickNotificationIntent(binding);
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    handleClickNotificationIntent(binding);
  }

  // 处理推送通知Intent
  private void handleClickNotificationIntent(@NonNull ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
    binding.addOnNewIntentListener(this.notificationIntentListener);
    Intent startupIntent = activity.getIntent();
    if (startupIntent == null) return;
    PushNotification initNotification = PushNotification.fromIntent(startupIntent);
    if (initNotification == null) return;

    PushEventDispatcher.setInitPushNotification(initNotification);
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
        PushEventDispatcher.unbind(Channel.TOKEN);
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
        PushEventDispatcher.unbind(Channel.NOTIFICATION_CLICK);
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
        PushEventDispatcher.unbind(Channel.NOTIFICATION_ARRIVED);
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
        PushEventDispatcher.unbind(Channel.TEST);
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
          } catch (IllegalArgumentException e) {
            Log.e(LogUtils.tag, "unknown intent type : " + type);
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
      Log.e(LogUtils.tag, e.toString());
    }

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (Method.valueOf(call.method)) {
      case getHuaweiToken:
        String hwAppId = call.argument("appId");
        HwUtils.getToken(context, hwAppId);
        break;
      case getMiToken:
        String appId = call.argument("appId");
        String appKey = call.argument("appKey");
        if (appId == null || appKey == null) {
          Log.e(LogUtils.tag, "getMiToken: no parameter appId or appKey");
          return;
        }
        MiPushClient.registerPush(context, appId, appKey);
        break;
      case getInitialNotification: // 获取点击通知打开app的消息
        PushNotification init = PushEventDispatcher.getInitNotification();
        if (init != null) {
          LogUtils.d(init.toJson());
        }
        result.success(init == null ? null : init.toJson());
        break;
      case getBrand:
        result.success(Build.MANUFACTURER);
        break;
      case getDeviceInfo:
        Map<String, String> device = new HashMap<>();
        device.put("brand", Build.BRAND);
        device.put("manufacturer", Build.MANUFACTURER);
        result.success(device);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  @Override
  public void onDetachedFromActivity() {
    this.activity = null;
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    this.activity = null;
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
