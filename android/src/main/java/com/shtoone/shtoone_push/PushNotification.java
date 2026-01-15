package com.shtoone.shtoone_push;

import android.content.Intent;
import android.os.Bundle;

import com.huawei.hms.push.RemoteMessage;
import com.shtoone.shtoone_push.Utils.MapUtils;
import com.shtoone.shtoone_push.Utils.Utils;
import com.shtoone.shtoone_push.constants.Brand;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PushNotification {
  private final String title;
  private final String content;
  private final Map<String, Object> extraData;

  public PushNotification(String title, String content, Map<String, Object> extraData) {
    this.title = title != null ? title : "";
    this.content = content != null ? content : "";
    this.extraData = extraData != null ? extraData : new HashMap<>();
  }

  /* ===================== Factory ===================== */

  public static PushNotification fromMap(Map<String, Object> map) {
    if (map == null) return new PushNotification("", "", null);

    String title = valueOf(map.get("title"));
    String content = valueOf(map.get("content"));

    Map<String, Object> extra =
        map.get("extraData") instanceof Map
            ? (Map<String, Object>) map.get("extraData")
            : new HashMap<>();

    return new PushNotification(title, content, extra);
  }

  public static PushNotification fromJson(String json) {
    try {
      JSONObject obj = new JSONObject(json);
      Map<String, Object> map = jsonToMap(obj);
      return fromMap(map);
    } catch (Exception e) {
      return new PushNotification("", "", null);
    }
  }

  public static PushNotification fromIntent(Intent intent) {
    PushNotification pn = null;
    Brand brand = Utils.getBrand();
    if (brand == Brand.XIAOMI) {
      MiPushMessage msg = (MiPushMessage) intent.getSerializableExtra(PushMessageHelper.KEY_MESSAGE);
      if (msg == null) return null;
      pn = PushNotification.fromMiPushMessage(msg);
    } else if (brand == Brand.HUAWEI) {
      Bundle bundle = intent.getExtras();
      if (bundle == null) return null;
      Map<String, Object> extras = MapUtils.toMap(Utils.convertJSONObject(bundle));
      pn = new PushNotification("huawei title", "huawei content", extras);
    }

    return pn;
  }

  public static PushNotification fromMiPushMessage(MiPushMessage message) {
    try {
      JSONObject obj = new JSONObject(message.getContent());
      Map<String, Object> map = jsonToMap(obj);
      return new PushNotification(message.getTitle(), message.getDescription(), map);
    } catch (Exception e) {
      return new PushNotification("", "", null);
    }
  }

  public static PushNotification fromHuaweiPushMessage(RemoteMessage message) {
    RemoteMessage.Notification notification = message.getNotification();
    if (notification == null) return null;
    Map<String, Object> map = new HashMap<>(message.getDataOfMap());
    return new PushNotification(notification.getTitle(), notification.getBody(), map);
  }

  /* ===================== 导出 ===================== */

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("title", title);
    map.put("content", content);
    map.put("extraData", extraData);
    return map;
  }

  public String toJson() {
    return new JSONObject(toMap()).toString();
  }

  /* ===================== Getter ===================== */

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public Map<String, Object> getExtraData() {
    return extraData;
  }

  /* ===================== 工具 ===================== */

  private static String valueOf(Object o) {
    return o == null ? "" : String.valueOf(o);
  }

  private static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
    Map<String, Object> map = new HashMap<>();
    Iterator<String> keys = json.keys();
    while (keys.hasNext()) {
      String key = keys.next();
      Object value = json.get(key);
      map.put(key, value);
    }
    return map;
  }
}
