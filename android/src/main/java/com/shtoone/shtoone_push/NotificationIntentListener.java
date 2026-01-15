package com.shtoone.shtoone_push;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.shtoone.shtoone_push.constants.Channel;

import io.flutter.plugin.common.PluginRegistry;

public class NotificationIntentListener implements PluginRegistry.NewIntentListener {
  @Override
  public boolean onNewIntent(@NonNull Intent intent) {
    handleIntent(intent);
    return false;
  }

  public void handleIntent(Intent intent) {
    PushNotification pn = PushNotification.fromIntent(intent);
    if (pn == null) return;

    PushEventDispatcher.send(Channel.NOTIFICATION_CLICK, pn.toJson());
  }
}
