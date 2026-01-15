package com.shtoone.shtoone_push.Utils;

import android.os.Build;
import android.os.Bundle;

import com.shtoone.shtoone_push.constants.Brand;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class Utils {
  public static Brand getBrand() {
    String brand = Build.MANUFACTURER.toLowerCase();
    if (brand.contains("huawei")) {
      return Brand.HUAWEI;
    } else if (brand.contains("xiaomi")) {
      return Brand.XIAOMI;
    }
    return Brand.UNKNOW;
  }

  public static JSONObject convertJSONObject(Bundle bundle) {
    try {
      JSONObject json = new JSONObject();
      if (bundle != null) {
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
          Object value = bundle.get(key);
          if (value instanceof Bundle) {
            json.put(key, convertJSONObject((Bundle) value));
          } else {
            json.put(key, JSONObject.wrap(value));
          }
        }
      }
      return json;
    } catch (JSONException e) {
      return null;
    }
  }
}
