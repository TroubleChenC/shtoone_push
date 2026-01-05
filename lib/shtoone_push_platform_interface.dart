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
}
