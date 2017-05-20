package com.flt.sampleservice;

import android.app.Application;
import android.content.Intent;

public class DemonstrationApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Intent i = new Intent(this, DemonstrationService.class);
    startService(i);
  }

}
