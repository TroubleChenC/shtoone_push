import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:shtoone_push/constants/constants.dart';

import 'shtoone_push_platform_interface.dart';

/// An implementation of [ShtoonePushPlatform] that uses method channels.
class MethodChannelShtoonePush extends ShtoonePushPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('shtoone_push');

  final tokenEventChannel = const EventChannel(Constants.getTokenEvent);
  final testEventChannel = const EventChannel(Constants.testEvent);
  final notificationOpenEventChannel = const EventChannel(
    Constants.notificationClickEvent,
  );
  final notificationArrivedEventChannel = const EventChannel(
    Constants.notificationArrivedEvent,
  );

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>(
      'getPlatformVersion',
    );
    return version;
  }

  @override
  Future<String?> getBrand() async {
    return await methodChannel.invokeMethod<String>(Constants.getBrand);
  }

  @override
  Future<Map<String, dynamic>?> getDeviceInfo() async {
    final deviceInfo = await methodChannel.invokeMethod(
      Constants.getDeviceInfo,
    );
    return Map<String, dynamic>.from(deviceInfo);
  }

  @override
  void getMiToken({required String appId, required String appKey}) {
    methodChannel.invokeMethod(Constants.getMiToken, {
      'appId': appId,
      'appKey': appKey,
    });
  }

  @override
  void getHuaweiToken(String appId) {
    methodChannel.invokeMethod(Constants.getHuaweiToken, {'appId': appId});
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

  @override
  Future<Map<String, dynamic>?> getInitialNotification() async {
    final jsonString = await methodChannel.invokeMethod<String>(
      Constants.getInitialNotification,
    );
    if (jsonString == null) {
      return null;
    }
    return Map<String, dynamic>.from(json.decode(jsonString));
  }

  @override
  Stream<Map<String, dynamic>> onNotificationArrived() {
    return notificationArrivedEventChannel
        .receiveBroadcastStream()
        .map((dynamic event) => json.decode(event))
        .cast<Map<String, dynamic>>();
  }
}
