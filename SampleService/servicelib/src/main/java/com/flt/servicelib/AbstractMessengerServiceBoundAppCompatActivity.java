package com.flt.servicelib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * <p>
 * A simple abstract activity that creates and binds a MessagingServicConnection whenever it is in
 * use. You can then use this connection to send messages to the service.
 * </p>
 * <p>
 * NB. If only bound, services will stop when all objects that have bound to it unbind. To prevent
 * this, you must also create an Application object, and modify your manifest to use it. That
 * Application object should start the service when the app launches.
 * </p>
 * <p>
 * The <b>onBound</b> method is for UI updates: use it to show or hide UI elements based on whether
 * the service itself is available.
 * </p>
 */
public abstract class AbstractMessengerServiceBoundAppCompatActivity extends AbstractPermissionExtensionAppCompatActivity {

  protected ComponentName serviceComponentName;
  protected MessagingServiceConnection connection;
  protected boolean bound;

  protected ComponentName getServiceComponentName() {
    if (serviceComponentName == null) {
      serviceComponentName = createServiceComponentName();
    }
    return serviceComponentName;
  }

  /**
   * Generate a ComponentName to pass to the Android system to request access to a service in another process,
   * eg.
   *
   * return new ComponentName("com.flt.sampleservice", "com.flt.sampleservice.DemonstrationMessagingService");
   */
  protected abstract ComponentName createServiceComponentName();

  protected void bindToMessenger() {
    connection = new MessagingServiceConnection();

    connection.setListener(new MessagingServiceConnection.Listener() {
      @Override
      public void onConnected(MessagingServiceConnection source) {
        bound = true;
        onBoundChanged(true);
      }

      @Override
      public void onDisconnected(MessagingServiceConnection source) {
        bound = false;
        onBoundChanged(false);
      }
    });

    Intent intent = new Intent();
    intent.setComponent(getServiceComponentName());
    bindService(intent, connection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStart() {
    super.onStart();
    bindToMessenger();
  }

  @Override
  protected void onStop() {
    super.onStop();

    if (bound) {
      connection.setListener(null);
      unbindService(connection);
      bound = false;
      onBoundChanged(bound);
    }
  }

  /**
   * Override to implement UI changes that reflect availability of the Service (ie. show/hide/enable/disable UI compoennts).
   */
  protected abstract void onBoundChanged(boolean isBound);
}
