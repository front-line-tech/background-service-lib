package com.flt.sampleservice;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.flt.servicelib.AbstractBackgroundMessengerService;
import com.flt.servicelib.BackgroundServiceConfig;

import java.util.Date;

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
        MainActivity.class);

    return config;
  }

  @Override
  protected String[] getRequiredPermissions() {
    return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
  }

  @Override
  protected Handler createMessageHandler() {
    return new IncomingHandler();
  }

  private class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      informUser(getString(R.string.toast_message_received, msg.what));
    }
  }
}