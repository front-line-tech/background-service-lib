package com.flt.servicelib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * <p>
 *   A broadcast receiver that listens for ACTION_BOOT_COMPLETED. You must list this receiver in
 *   the app's manifest file. The receiver should filter for android.intent.action.BOOT_COMPLETED.
 *   <i>See manifest file in the sample implementation for reference.</i>
 * </p>
 * <p>
 *   Known boot completion intents to register your receiver for in the manifest:
 *   <ul>
 *     <li>android.intent.action.BOOT_COMPLETED</li>
 *     <li>android.intent.action.LOCKED_BOOT_COMPLETED</li>
 *     <li>android.intent.action.QUICKBOOT_POWERON</li>
 *   </ul>
 * </p>
 * <p>
 *   Not receiving ACTION_BOOT_COMPLETED? There are several things to check:
 *   <ul>
 *     <li>Have you registered for RECEIVE_BOOT_COMPLETED permission in your manifest?</li>
 *     <li>Have you registered your receiver for the known boot completion intents?</li>
 *     <li>Has your user got battery optimisations for your app in the phone's settings?</li>
 *     <li>Have you exported your receiver? (Not recommended.)</li>
 *   </ul>
 *   In tests, some devices take several minutes before they'll decide that boot has actually
 *   completed before broadcasting the intent to your app.
 * </p>
 *
 * @param <LaunchService> the class of the service to launch
 */
public abstract class AbstractBootReceiver<LaunchService> extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
        Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction()) ||
        "android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())) {
      Log.i("BootReceiver", "Starting service: " + getServiceClass().getCanonicalName());

      Intent i = new Intent(context, getServiceClass());
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && shouldStartAsForegroundService()) {
        context.startForegroundService(i);
      } else {
        context.startService(i);
      }

    } else {
      Log.w("BootReceiver", "Intent action was not: " + Intent.ACTION_BOOT_COMPLETED);
    }
  }

  /**
   * Override and return true to use context.startForegroundService (Android O and above).
   * If not running Android O+ then the receiver will fall back to regular context.startService.
   */
  protected abstract boolean shouldStartAsForegroundService();

  /**
   * Override and return the class of the service you wish to start - eg. MyService.class
   */
  protected abstract Class<LaunchService> getServiceClass();
}
