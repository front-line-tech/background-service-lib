package com.flt.servicelib;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DemonstrationApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    Intent i = new Intent(this, DemonstrationBindingService.class);
    Intent j = new Intent(this, DemonstrationMessagingService.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(i);
      startForegroundService(j);
    } else {
      startService(i);
      startService(j);
    }

  }

  public static String[] permissions = new String[] {
    WRITE_EXTERNAL_STORAGE,
    RECEIVE_BOOT_COMPLETED
  };

}
