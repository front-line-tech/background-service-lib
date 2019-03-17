package com.flt.servicelib;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class DemonstrationMessagingService extends AbstractBackgroundMessengerService {

  @Override
  public void onCreate() {
    super.onCreate();
    informUser(R.string.toast_messaging_service_started);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    informUser(R.string.toast_messaging_service_destroyed);
  }

  @Override
  protected void restoreFrom(SharedPreferences prefs) {
    Log.i("MessagingService", "Service started. Restoring from private shared preferences.");
    // TODO: restore your service from these shared preferences
    // TODO: provide defaults where these preferences have not yet been populated
  }

  @Override
  protected void storeTo(SharedPreferences.Editor editor) {
    Log.i("MessagingService", "Service stopping. Storing state in private shared preferences.");
    // TODO: record the state of your service here so it can be easily restored
  }

  @Override
  protected BackgroundServiceConfig configure(BackgroundServiceConfig config) {
    config.setNotification(
      getString(R.string.messaging_service_notification_title),
      getString(R.string.messaging_service_notification_content),
      getString(R.string.messaging_service_notification_ticker),
      R.mipmap.ic_service_messaging,
      ServiceAndMessengerActivity.class,
      getString(R.string.foreground_channel_name),
      getString(R.string.foreground_channel_description),
      NotificationManagerCompat.IMPORTANCE_MAX
    );

    return config;
  }

  @Override
  protected String[] getRequiredPermissions() {
    return DemonstrationApp.permissions;
  }

  @Override
  protected void onMessageReceived(Message message) {
    informUser(getString(R.string.toast_service_message_received, message.what));
    Message response = Message.obtain();
    response.what = message.what+1;
    response.replyTo = receiver;
    response.setData(new Bundle());

    try {
      replyTo(message, response);
    } catch (Exception e) {
      informUser("Exception encountered replying from Service: " + e.getMessage());
    }
  }

}