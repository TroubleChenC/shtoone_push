package com.shtoone.shtoone_push.MessageReceiver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.shtoone.shtoone_push.Channel;
import com.shtoone.shtoone_push.PushIntent;
import com.shtoone.shtoone_push.PushEventDispatcher;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

  public static Map<String, Object> getMessagePayload(MiPushMessage message) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("title", message.getTitle());
    payload.put("content", message.getContent());
    payload.put("extra", message.getExtra());
    return payload;
  }

  public static String toJson(MiPushMessage message) {
    return new JSONObject(getMessagePayload(message)).toString();
  }

  @Override
  public void onNotificationMessageClicked(Context context, MiPushMessage message) {
    mMessage = message.getContent();
    if (!TextUtils.isEmpty(message.getTopic())) {
      mTopic = message.getTopic();
    } else if (!TextUtils.isEmpty(message.getAlias())) {
      mAlias = message.getAlias();
    } else if (!TextUtils.isEmpty(message.getUserAccount())) {
      mUserAccount = message.getUserAccount();
    }

    String json = toJson(message);
    PushIntent.notifyMainProcess(context, json, Channel.NOTIFICATION_CLICK);
  }

  @Override
  public void onNotificationMessageArrived(Context context, MiPushMessage message) {
    Log.d("shtoone_push", "onNotificationMessageArrived");
    mMessage = message.getContent();
    if (!TextUtils.isEmpty(message.getTopic())) {
      mTopic = message.getTopic();
    } else if (!TextUtils.isEmpty(message.getAlias())) {
      mAlias = message.getAlias();
    } else if (!TextUtils.isEmpty(message.getUserAccount())) {
      mUserAccount = message.getUserAccount();
    }

    String json = toJson(message);
    PushIntent.notifyMainProcess(context, json, Channel.NOTIFICATION_ARRIVED);
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