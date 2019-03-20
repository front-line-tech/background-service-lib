package com.flt.servicelib;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DemonstrationBootReceiver extends AbstractBootReceiver<DemonstrationBindingService> {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("BootReceiver", "Received: " + intent.getAction());
    try {
      super.onReceive(context, intent);
    } catch (Exception e) {
      Log.e("BootReceiver", "Could not initiate DemonstrationBindingService.", e);
    }
  }

  @Override
  protected boolean shouldStartAsForegroundService() {
    return true;
  }

  @Override
  protected Class<DemonstrationBindingService> getServiceClass() {
    return DemonstrationBindingService.class;
  }



}
