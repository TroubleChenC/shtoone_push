# shtoone_push

整合国内手机推送服务（自用）

## Getting Started

### 小米

首先将 MiPush SDK 的 AAR 包如 MiPush_SDK_Client_xxx.aar 复制到 `project/android/app/libs/` 目录，然后在项目的`project/android/app/build.gradle.kts`中声明依赖：

``` kotlin
dependencies {
    implementation(files("libs/MiPush_SDK_Client_6_0_1-C_3rd.aar"))
}
```

如果您的应用使用了混淆，您需要使用下面的代码keep自定义的BroadcastReceiver  
`project/android/app/proguard-rules.pro`
```
 -keep class com.shtoone.shtoone_push.MessageReceiver.MiMessageReceiver {*;}
```


### 华为
1. 将“agconnect-services.json”文件拷贝到android目录下`project/android/app`;
2. 添加HUAWEI AGC插件以及Maven代码库`project/android/build.gradle.kts`:
    ```kotlin
    buildscript {
        repositories {
            google()
            mavenCentral()
            maven(url = "https://developer.huawei.com/repo/")
        }
        dependencies {
            classpath ("com.android.tools.build:gradle:8.7.0")
            classpath("com.huawei.agconnect:agcp:1.9.1.304")
        }
    }
    ```
3. 应用HUAWEI AGC插件`project/android/app/build.gradle.kts`:
    ```
   plugins {
      id("com.android.application")
      id("kotlin-android")
      id("dev.flutter.flutter-gradle-plugin")
   
      // 添加如下配置
      id ("com.huawei.agconnect")
    }
   ```
