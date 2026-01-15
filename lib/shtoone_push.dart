import 'shtoone_push_platform_interface.dart';

class ShtoonePush {
  /// 获取手机品牌 eg. HUAWEI XIAOMI OPPO...
  static Future<String?> getBrand() {
    return ShtoonePushPlatform.instance.getBrand();
  }

  /// 获取设备信息
  static Future<Map<String, dynamic>?> getDeviceInfo() {
    return ShtoonePushPlatform.instance.getDeviceInfo();
  }

  /// 注册小米regId,注册结果需要监听getTokenStream获取
  static void getMiToken({required String appId, required String appKey}) {
    ShtoonePushPlatform.instance.getMiToken(appId: appId, appKey: appKey);
  }

  static void getHuaweiToken(String appId) {
    ShtoonePushPlatform.instance.getHuaweiToken(appId);
  }

  /// 获取/注册token事件stream
  static Stream<String> get getTokenStream {
    return ShtoonePushPlatform.instance.getTokenStream();
  }

  /// 测试
  static Stream<String> get getTestStream {
    return ShtoonePushPlatform.instance.getTestStream();
  }

  /// 获取点击打开app的那条消息，一般用于app冷启动获取消息
  static Future<Map<String, dynamic>?> getInitialNotification() {
    return ShtoonePushPlatform.instance.getInitialNotification();
  }

  /// 点击通知消息打开app事件stream
  static Stream<Map<String, dynamic>> get onNotificationOpenedApp {
    return ShtoonePushPlatform.instance.onNotificationOpenedApp();
  }

  /// 消息到达事件stream
  static Stream<Map<String, dynamic>> get onNotificationArrived {
    return ShtoonePushPlatform.instance.onNotificationArrived();
  }

  static void emitTestEvent() {
    ShtoonePushPlatform.instance.emitTestEvent();
  }
}
