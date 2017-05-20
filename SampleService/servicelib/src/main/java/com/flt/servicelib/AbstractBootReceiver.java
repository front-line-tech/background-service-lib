package com.flt.servicelib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * A broadcast receiver that listens for ACTION_BOOT_COMPLETED. You must list this receiver in the
 * app's manifest file. Uses-permission RECEIVE_BOOT_COMPLETED is already specified, but the
 * receiver should filter for android.intent.action.BOOT_COMPLETED. See manifest file in the sample
 * implementation for reference.
 *
 * @param <LaunchService> the class of the service to launch
 */
public abstract class AbstractBootReceiver<LaunchService> extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    // on boot completed intents, start the service
    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
      context.startService(new Intent(context, getServiceClass()));
    }
  }

  /**
   * Override and return the class of the service you wish to start - eg. MyService.class
   */
  protected abstract Class<LaunchService> getServiceClass();
}
