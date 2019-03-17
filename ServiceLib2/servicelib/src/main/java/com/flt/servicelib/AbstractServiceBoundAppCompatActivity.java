package com.flt.servicelib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <p>
 * A simple abstract activity that binds to the specified service whenever it is in use, and unbinds
 * whenever it is paused. The activity will launch the service as it starts with an intent (even if
 * the service is already running).
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
 * @param <ServiceClass> the class of the service you wish to bind to.
 * @param <ServiceInterface> the interface to present the service as.
 */
public abstract class AbstractServiceBoundAppCompatActivity<ServiceClass, ServiceInterface> extends AbstractPermissionExtensionAppCompatActivity {
  private static final String TAG = "ServiceBoundActivity";

  protected Class<ServiceClass> inferredServiceClass;
  protected ServiceInterface service;
  protected boolean bound;

  /**
   * Returns the class of the service this Activity should bind to - derived from the generic type parameters.
   * NB. The first generic type parameter is the Service class, the second is the class of the Service's interface.
   */
  protected Class<ServiceClass> getServiceClass() {
    if (inferredServiceClass == null) {
      try {
        Type mySuperclass = getClass().getGenericSuperclass();
        Type tType = ((ParameterizedType) mySuperclass).getActualTypeArguments()[0]; // 1st arg
        String className = tType.toString().split(" ")[1]; // parse it as a string
        inferredServiceClass = (Class<ServiceClass>) Class.forName(className);
      } catch (Exception e) {
        throw new RuntimeException("Could not determine Service class.", e);
      }
    }
    return inferredServiceClass;
  }


  @Override
  protected void onStart() {
    super.onStart();
    Intent intent = new Intent(this, getServiceClass());
    bindService(intent, connection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (bound) {
      unbindService(connection);
      bound = false;
      onBoundChanged(bound);
    }
  }

  /**
   * Override to implement UI changes that reflect availability of the Service (ie. show/hide/enable/disable UI compoennts).
   */
  protected abstract void onBoundChanged(boolean isBound);

  private ServiceConnection connection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName className, IBinder binder) {
      // We've bound to Service, cast the IBinder and get LocalService instance
      service = ((IServiceBinder<ServiceInterface>)binder).getService();
      bound = true;
      onBoundChanged(bound);
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      bound = false;
      onBoundChanged(bound);
    }
  };

}
