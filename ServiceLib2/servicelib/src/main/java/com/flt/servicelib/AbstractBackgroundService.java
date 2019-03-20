package com.flt.servicelib;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public abstract class AbstractBackgroundService extends Service {

  private static final String TAG = "BackgroundService";

  protected IBinder binder;
  protected Notification foreground_notification;
  protected NotificationChannel foreground_channel;
  protected BackgroundServiceConfig config;

  private static int NEXT_FOREGROUND_ID = 8000; // shared counter to prevent clashes between instances
  protected int foreground_id; // the id of this instance's notification

  @Override
  public void onCreate() {
    super.onCreate();
    foreground_id = NEXT_FOREGROUND_ID++;
    binder = createBinder();
    config = configure(new BackgroundServiceConfig());
    SharedPreferences prefs = getSharedPreferences(getClass().getCanonicalName(), MODE_PRIVATE);
    restoreFrom(prefs);
    foreground_channel = buildForegroundChannel();
    if (config.show_notification) { goToForeground(); }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    SharedPreferences.Editor editor = getSharedPreferences(getClass().getCanonicalName(), MODE_PRIVATE).edit();
    storeTo(editor);
    editor.commit();
    if (foreground_notification != null) { quitForeground(); }
  }

  protected abstract void restoreFrom(SharedPreferences prefs);
  protected abstract void storeTo(SharedPreferences.Editor editor);

  /**
   * Provides a Binder object to allow Activities to bind to this service with the appropriate interface.
   */
  protected abstract IBinder createBinder();

  @Override
  public IBinder onBind(Intent intent) { return binder; }

  /**
   * Provides a configuration object for this Service.
   */
  protected abstract BackgroundServiceConfig configure(BackgroundServiceConfig defaults);

  protected void goToForeground() {
    try {
      foreground_notification = buildStandardNotification();
      startForeground(foreground_id, foreground_notification);
    } catch (Exception e) {
      Log.e(TAG, "Could not go to foreground with notification.", e);
    }
  }

  /**
   * Override this method to receive intents as instructions.
   * Return START_STICKY to ensure the service restarts if stopped.
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  protected NotificationChannel buildForegroundChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Log.d(TAG, "Building foreground channel for Android >= Oreo");

      NotificationManager mgr = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
      String id = getClass().getCanonicalName() + ".notifications.foreground";

      // The user-visible name of the channel.
      CharSequence name = config.notification_channel_name;
      String description = config.notification_channel_description;
      int importance = config.notification_channel_importance;

      //int importance = NotificationManager.IMPORTANCE_MAX;
      NotificationChannel channel = new NotificationChannel(id, name, importance);
      channel.setDescription(description);
      channel.enableLights(false);
      channel.enableVibration(false);
      mgr.createNotificationChannel(channel);

      return channel;
    }else {
      Log.d(TAG, "Android < Oreo, no notification channel required.");
      return null;
    }
  }

  protected Notification buildStandardNotification() {
    Notification.Builder builder = new Notification.Builder(this);

    builder.setContentTitle(config.notification_title);
    builder.setContentText(config.notification_content);
    builder.setSmallIcon(config.notification_icon);
    builder.setPriority(config.notification_priority);

    if (config.notification_activity_class != null) {
      Intent launchIntent = new Intent(this, config.notification_activity_class);
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
      builder.setContentIntent(pendingIntent);
    }

    if (config.notification_ticker != null) {
      builder.setTicker(config.notification_ticker);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && foreground_channel != null) {
      builder.setChannelId(foreground_channel.getId());
    }

    return builder.build();
  }

  protected void quitForeground() {
    stopForeground(true);
    foreground_notification = null;
  }

  /**
   * @return true if the overlay permission is granted to this app.
   */
  public boolean hasOverlayPermission() {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this);
  }

  /**
   * @return true if any permission from getRequiredPermissions is not granted.
   */
  protected boolean anyOutstandingPermissions() {
    for (int i = 0; i < getRequiredPermissions().length; i++) {
      if (needsPermission(getRequiredPermissions()[i])) { return true; }
    }
    return false;
  }

  /**
   * Implements a list of permissions this service requires to run.
   * You must check for these using anyOutstandingPermissions to prevent exceptions at runtime.
   * @return a String[] of permissions. Android permissions are found in Manifest.permissions.
   */
  protected abstract String[] getRequiredPermissions();

  /**
   * Checks for the given permission.
   * @param permission a permission (Android permissions are available in Manifest.permissions)
   * @return true if this permission is still required
   */
  protected boolean needsPermission(String permission) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED;
    } else {
      return false; // permission automatically granted before Marshmallow
    }
  }


  protected void informUser(int string_resource) {
    informUser(getString(string_resource));
  }

  protected void informUser(String msg) {
    final String finalMsg = msg;
    Handler h = new Handler(getMainLooper());
    h.post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(AbstractBackgroundService.this, finalMsg, Toast.LENGTH_SHORT).show();
      }
    });
  }

}

