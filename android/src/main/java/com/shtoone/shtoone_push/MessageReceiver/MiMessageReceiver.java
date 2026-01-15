package com.shtoone.shtoone_push.MessageReceiver;

import android.content.Context;
import android.text.TextUtils;

import com.shtoone.shtoone_push.constants.Channel;
import com.shtoone.shtoone_push.PushIntent;
import com.shtoone.shtoone_push.PushEventDispatcher;
import com.shtoone.shtoone_push.PushNotification;
import com.shtoone.shtoone_push.constants.LogUtils;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;


public class MiMessageReceiver extends PushMessageReceiver {
  private String mRegId;
  private long mResultCode = -1;
  private String mReason;
  private String mCommand;
  private String mMessage;
  private String mTopic;
  private String mAlias;
  private String mUserAccount;
  private String mStartTime;
  private String mEndTime;

  @Override
  public void onNotificationMessageClicked(Context context, MiPushMessage message) {
    LogUtils.d("onNotificationMessageClicked");

    mMessage = message.getContent();
    if (!TextUtils.isEmpty(message.getTopic())) {
      mTopic = message.getTopic();
    } else if (!TextUtils.isEmpty(message.getAlias())) {
      mAlias = message.getAlias();
    } else if (!TextUtils.isEmpty(message.getUserAccount())) {
      mUserAccount = message.getUserAccount();
    }

    PushNotification notification = PushNotification.fromMiPushMessage(message);
    PushIntent.notifyMainProcess(context, notification.toJson(), Channel.NOTIFICATION_CLICK);
  }

  @Override
  public void onNotificationMessageArrived(Context context, MiPushMessage message) {
    LogUtils.d("onNotificationMessageArrived");

    mMessage = message.getContent();
    if (!TextUtils.isEmpty(message.getTopic())) {
      mTopic = message.getTopic();
    } else if (!TextUtils.isEmpty(message.getAlias())) {
      mAlias = message.getAlias();
    } else if (!TextUtils.isEmpty(message.getUserAccount())) {
      mUserAccount = message.getUserAccount();
    }

    PushNotification notification = PushNotification.fromMiPushMessage(message);
    PushIntent.notifyMainProcess(context, notification.toJson(), Channel.NOTIFICATION_ARRIVED);
  }

  @Override
  public void onCommandResult(Context context, MiPushCommandMessage message) {
    String command = message.getCommand();
    List<String> arguments = message.getCommandArguments();
    String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
    String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
    if (MiPushClient.COMMAND_REGISTER.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mRegId = cmdArg1;
        PushEventDispatcher.send(Channel.TOKEN, mRegId);
      }
    } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mAlias = cmdArg1;
      }
    } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mAlias = cmdArg1;
      }
    } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mTopic = cmdArg1;
      }
    } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mTopic = cmdArg1;
      }
    } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mStartTime = cmdArg1;
        mEndTime = cmdArg2;
      }
    }
  }

  @Override
  public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
    LogUtils.d("onReceiveRegisterResult");

    String command = message.getCommand();
    List<String> arguments = message.getCommandArguments();
    String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
    String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
    if (MiPushClient.COMMAND_REGISTER.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mRegId = cmdArg1;
        PushIntent.notifyMainProcess(context, mRegId, Channel.TOKEN);
      }
    }
  }
}