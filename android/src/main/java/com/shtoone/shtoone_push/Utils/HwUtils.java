package com.shtoone.shtoone_push.Utils;

import android.content.Context;
import android.os.Build;

import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.shtoone.shtoone_push.PushIntent;
import com.shtoone.shtoone_push.constants.Channel;
import com.shtoone.shtoone_push.constants.LogUtils;

import io.flutter.Log;

public class HwUtils {
  public static boolean bHuaWei() {
    String brand = Build.BRAND.toLowerCase();
    return brand.contains("huawei") || brand.contains("hw");
  }

  public static void getToken(Context context, String appId) {
    new Thread() {
      @Override
      public void run() {
        try {
          String token = HmsInstanceId.getInstance(context).getToken(appId, "HCM");
          Log.d(LogUtils.tag, "hw getToken:" + token);
          PushIntent.notifyMainProcess(context, token, Channel.TOKEN);
        } catch (ApiException e) {
          Log.e(LogUtils.tag, "get token failed, " + e);
        }
      }
    }.start();
  }
}
