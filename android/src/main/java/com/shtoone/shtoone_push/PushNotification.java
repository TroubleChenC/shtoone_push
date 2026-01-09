package com.shtoone.shtoone_push;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PushNotification {
  private final String title;
  private final String content;
  private final Map<String, String> extraData;

  public PushNotification(String title, String content, Map<String, String> extraData) {
    this.title = title != null ? title : "";
    this.content = content != null ? content : "";
    this.extraData = extraData != null ? extraData : new HashMap<>();
  }

  /* ===================== Factory ===================== */

  public static PushNotification fromMap(Map<String, Object> map) {
    if (map == null) return new PushNotification("", "", null);

    String title = valueOf(map.get("title"));
    String content = valueOf(map.get("content"));

    Map<String, String> extra =
        map.get("extraData") instanceof Map
            ? (Map<String, String>) map.get("extraData")
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

  public Map<String, String> getExtraData() {
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
