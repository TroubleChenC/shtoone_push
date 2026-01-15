package com.shtoone.shtoone_push.MessageReceiver;


import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.shtoone.shtoone_push.PushIntent;
import com.shtoone.shtoone_push.PushNotification;
import com.shtoone.shtoone_push.constants.Channel;
import com.shtoone.shtoone_push.constants.LogUtils;

import io.flutter.Log;

public class HuaWeiMessageService extends HmsMessageService {
  @Override
  public void onNewToken(String s) {
    super.onNewToken(s);

    Log.d(LogUtils.tag, "hw onNewToken:" + s);

    PushIntent.notifyMainProcess(getApplicationContext(), s, Channel.TOKEN);
  }

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    Log.d(LogUtils.tag, "onMessageReceived:" + remoteMessage.toString());

    PushNotification pushNotification = PushNotification.fromHuaweiPushMessage(remoteMessage);
    if (pushNotification == null) return;
    PushIntent.notifyMainProcess(getApplicationContext(), pushNotification.toJson(), Channel.NOTIFICATION_ARRIVED);
  }
}
