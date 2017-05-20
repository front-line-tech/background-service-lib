package com.flt.sampleservice;

import android.Manifest;

import com.flt.servicelib.AbstractBackgroundService;
import com.flt.servicelib.BackgroundServiceConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DemonstrationService extends AbstractBackgroundService<DemonstrationServiceInterface> implements DemonstrationServiceInterface {

  private int counter;
  private Date started;

  @Override
  public void onCreate() {
    super.onCreate();
    this.counter = 0;
    this.started = new Date();
    informUser(R.string.toast_service_started);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    informUser(R.string.toast_service_destroyed);
  }

  @Override
  protected BackgroundServiceConfig configure(BackgroundServiceConfig config) {
    config.setNotification(
        getString(R.string.service_notification_title),
        getString(R.string.service_notification_content),
        getString(R.string.service_notification_ticker),
        R.mipmap.ic_service,
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
