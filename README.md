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
