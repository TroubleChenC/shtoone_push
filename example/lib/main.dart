import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
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
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    if (!mounted) return;
    ShtoonePush.getTokenStream.listen(
      (e) {
        print(e);
      },
      onError: (error) {
        print((error as PlatformException).message);
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
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
              onPressed: () {
                const appId = '2882303761520485640';
                const appKey = '5362048551640';
                ShtoonePush.getMiToken(appId: appId, appKey: appKey);
              },
              child: const Text('getToken'),
            ),
          ],
        ),
      ),
    );
  }
}
