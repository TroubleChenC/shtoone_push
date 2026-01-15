import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';
import 'dart:async';

import 'package:shtoone_push/shtoone_push.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
    // initPlatformState();
    initPermission();
  }

  Future<void> initPlatformState() async {
    if (!mounted) return;
    ShtoonePush.getTokenStream.listen(
      (e) {
        print('token:$e');
      },
      onError: (error) {
        print((error as PlatformException).message);
      },
    );

    final init = await ShtoonePush.getInitialNotification();
    if (init != null) {
      print(init);
    } else {
      print('no init notification');
    }

    // 消息监听
    ShtoonePush.onNotificationArrived.listen((e) {
      print('onNotificationArrived-------');
      print(e);
    });
    ShtoonePush.onNotificationOpenedApp.listen((e) {
      print('onNotificationOpenedApp-------');
      print(e);
    });
  }

  Future<void> initPermission() async {
    if (!await Permission.notification.request().isGranted) {
      print('no notification permission');
      await openAppSettings();
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(useMaterial3: false),
      home: Scaffold(
        appBar: AppBar(title: const Text('Plugin example app')),
        body: Column(
          children: [
            Center(child: Text('Running on: ')),
            ElevatedButton(
              onPressed: () async {
                final brand = await ShtoonePush.getBrand();
                print(brand);
              },
              child: const Text('getBrand'),
            ),
            ElevatedButton(
              onPressed: () async {
                final brand = await ShtoonePush.getBrand();
                if (brand != null && brand.toLowerCase() == 'huawei') {
                  const hwAppId = '116383695';
                  ShtoonePush.getMiToken(appId: hwAppId, appKey: '-');
                } else {
                  const appId = '2882303761520485640';
                  const appKey = '5362048551640';
                  ShtoonePush.getMiToken(appId: appId, appKey: appKey);
                }
              },
              child: const Text('getToken'),
            ),
            Builder(
              builder: (context) {
                return ElevatedButton(
                  onPressed: () {
                    showDialog(
                      context: context,
                      builder: (context) {
                        return Dialog(
                          insetPadding: const EdgeInsets.symmetric(
                            horizontal: 20,
                          ),
                          child: SizedBox(
                            width: MediaQuery.of(context).size.width,
                            child: const Padding(
                              padding: EdgeInsets.all(16),
                              child: Text('Dialog'),
                            ),
                          ),
                        );
                      },
                    );
                  },
                  child: const Text('dialog'),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}
