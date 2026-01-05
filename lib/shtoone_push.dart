import 'shtoone_push_platform_interface.dart';

class ShtoonePush {
  static Future<String?> getBrand() {
    return ShtoonePushPlatform.instance.getBrand();
  }

  static void getMiToken({required String appId, required String appKey}) {
    ShtoonePushPlatform.instance.getMiToken(appId: appId, appKey: appKey);
  }

  static Stream<String> get getTokenStream {
    return ShtoonePushPlatform.instance.getTokenStream();
  }

  static Stream<String> get getTestStream {
    return ShtoonePushPlatform.instance.getTestStream();
  }

  static Stream<Map<String, dynamic>> get onNotificationOpenedApp {
    return ShtoonePushPlatform.instance.onNotificationOpenedApp();
  }

  static void emitTestEvent() {
    ShtoonePushPlatform.instance.emitTestEvent();
  }
}
