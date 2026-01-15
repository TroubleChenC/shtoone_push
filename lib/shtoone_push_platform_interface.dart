import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'shtoone_push_method_channel.dart';

abstract class ShtoonePushPlatform extends PlatformInterface {
  /// Constructs a ShtoonePushPlatform.
  ShtoonePushPlatform() : super(token: _token);

  static final Object _token = Object();

  static ShtoonePushPlatform _instance = MethodChannelShtoonePush();

  /// The default instance of [ShtoonePushPlatform] to use.
  ///
  /// Defaults to [MethodChannelShtoonePush].
  static ShtoonePushPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [ShtoonePushPlatform] when
  /// they register themselves.
  static set instance(ShtoonePushPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> getBrand() {
    throw UnimplementedError('getBrand() has not been implemented.');
  }

  Future<Map<String, dynamic>?> getDeviceInfo() {
    throw UnimplementedError('getDeviceInfo() has not been implemented.');
  }

  void getMiToken({required String appId, required String appKey}) {
    throw UnimplementedError('getMiToken() has not been implemented.');
  }

  void getHuaweiToken(String appId) {
    throw UnimplementedError('getHuaweiToken() has not been implemented.');
  }

  Stream<String> getTokenStream() {
    throw UnimplementedError('getTokenStream() has not been implemented.');
  }

  Stream<String> getTestStream() {
    throw UnimplementedError('getTestStream() has not been implemented.');
  }

  void emitTestEvent() {
    throw UnimplementedError('emitTestEvent() has not been implemented.');
  }

  Stream<Map<String, dynamic>> onNotificationOpenedApp() {
    throw UnimplementedError(
      'onNotificationOpenedApp() has not been implemented.',
    );
  }

  Future<Map<String, dynamic>?> getInitialNotification() {
    throw UnimplementedError(
      'getInitialNotification() has not been implemented.',
    );
  }

  Stream<Map<String, dynamic>> onNotificationArrived() {
    throw UnimplementedError(
      'onNotificationArrived() has not been implemented.',
    );
  }
}
