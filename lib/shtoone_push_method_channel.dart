import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'shtoone_push_platform_interface.dart';

/// An implementation of [ShtoonePushPlatform] that uses method channels.
class MethodChannelShtoonePush extends ShtoonePushPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('shtoone_push');

  final tokenEventChannel = const EventChannel('token_event');
  final testEventChannel = const EventChannel('test_event');
  final notificationOpenEventChannel = const EventChannel('notification_click');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>(
      'getPlatformVersion',
    );
    return version;
  }

  @override
  Future<String?> getBrand() async {
    return await methodChannel.invokeMethod<String>('getBrand');
  }

  @override
  void getMiToken({required String appId, required String appKey}) {
    methodChannel.invokeMethod('getToken', {'appId': appId, 'appKey': appKey});
  }

  @override
  Stream<String> getTokenStream() {
    return tokenEventChannel.receiveBroadcastStream().cast<String>();
  }

  @override
  Stream<String> getTestStream() {
    return testEventChannel.receiveBroadcastStream().cast<String>();
  }

  @override
  void emitTestEvent() {
    methodChannel.invokeMethod('emit');
  }

  @override
  Stream<Map<String, dynamic>> onNotificationOpenedApp() {
    return notificationOpenEventChannel
        .receiveBroadcastStream()
        .map((dynamic event) => json.decode(event))
        .cast<Map<String, dynamic>>();
  }
}
