package com.flt.servicelib;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

public abstract class AbstractBackgroundService<ServiceInterface> extends Service {

  private static final String TAG = "AbstractBackgroundService";

  IServiceBinder<ServiceInterface> binder;
  Notification foreground_notification;
  BackgroundServiceConfig config;
  final int FOREGROUND_ID = 8001;

  @Override
  public void onCreate() {
    super.onCreate();
    binder = createBinder((ServiceInterface)this);
    config = configure(new BackgroundServiceConfig());
    if (config.show_notification) { goToForeground(); }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (foreground_notification != null) { quitForeground(); }
  }

  /**
   * Provides a Binder object to allow Activitites to bind to this service with the appropriate interface.
   */
  protected ServiceBinder<ServiceInterface> createBinder(ServiceInterface theService) {
    return new ServiceBinder<ServiceInterface>(theService);
  }

  @Override
  public IBinder onBind(Intent intent) { return binder; }

  /**
   * Provides a configuration object for this Service.
   */
  protected abstract BackgroundServiceConfig configure(BackgroundServiceConfig defaults);

  private void goToForeground() {
    foreground_notification = buildStandardNotification();
    startForeground(FOREGROUND_ID, foreground_notification);
  }

  /**
   * Override this method to receive intents as instructions.
   * Return START_STICKY to ensure the service restarts if stopped.
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  private Notification buildStandardNotification() {
    Intent launchIntent = new Intent(this, config.notification_activity_class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
    Notification.Builder builder = new Notification.Builder(this);
    builder.setContentTitle(config.notification_title);
    builder.setContentText(config.notification_content);
    builder.setSmallIcon(config.notification_icon);
    builder.setPriority(config.notification_priority);
    builder.setContentIntent(pendingIntent);
    if (config.notification_ticker != null) {
      builder.setTicker(config.notification_ticker);
    }
    return builder.build();
  }

  private void quitForeground() {
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
