#!/bin/bash

# 构建APK
flutter build apk --split-per-abi --target-platform=android-arm64

# 为不同架构的APK文件添加自定义名称
cp "build/app/outputs/flutter-apk/app-arm64-v8a-release.apk" "./apk/push.apk"
# 你可以继续添加其他架构的APK文件名称
