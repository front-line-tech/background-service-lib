package com.flt.sampleservice;

import android.app.Application;
import android.content.Intent;

public class DemonstrationApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    Intent i = new Intent(this, DemonstrationBindingService.class);
    startService(i);

    Intent j = new Intent(this, DemonstrationMessagingService.class);
    startService(j);
  }

}
