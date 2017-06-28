package com.flt.sampleservice;

import android.Manifest;
import android.content.SharedPreferences;
import android.util.Log;

import com.flt.servicelib.AbstractBackgroundBindingService;
import com.flt.servicelib.BackgroundServiceConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DemonstrationBindingService extends AbstractBackgroundBindingService<DemonstrationServiceInterface> implements DemonstrationServiceInterface {

  private int counter;
  private Date started;

  @Override
  public void onCreate() {
    super.onCreate();
    this.counter = 0;
    this.started = new Date();
    informUser(R.string.toast_binding_service_started);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    informUser(R.string.toast_binding_service_destroyed);
  }

  @Override
  protected void restoreFrom(SharedPreferences prefs) {
    Log.i("BindingService", "Service started. Restoring from private shared preferences.");
    // TODO: restore your service from these shared preferences
    // TODO: provide defaults where these preferences have not yet been populated
  }

  @Override
  protected void storeTo(SharedPreferences.Editor editor) {
    Log.i("BindingService", "Service stopping. Storing state in private shared preferences.");
    // TODO: record the state of your service here so it can be easily restored
  }

  @Override
  protected BackgroundServiceConfig configure(BackgroundServiceConfig config) {
    config.setNotification(
        getString(R.string.binding_service_notification_title),
        getString(R.string.binding_service_notification_content),
        getString(R.string.binding_service_notification_ticker),
        R.mipmap.ic_service_binding,
        MainActivity.class);

    return config;
  }

  @Override
  protected String[] getRequiredPermissions() {
    return new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE };
  }

  @Override
  public String doSomething() {
    if (anyOutstandingPermissions()) {
      return getString(R.string.toast_service_cannot_do_something);
    }

    String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(started);
    int count = counter++;
    return "The Demonstration Service service has done something " + count + " times since it started at: " + startTime;
  }


}
