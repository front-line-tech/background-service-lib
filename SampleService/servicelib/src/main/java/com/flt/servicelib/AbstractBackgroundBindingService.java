package com.flt.servicelib;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

public abstract class AbstractBackgroundBindingService<ServiceInterface> extends AbstractBackgroundService {

  @Override
  protected IBinder createBinder() {
    return new ServiceBinder<ServiceInterface>((ServiceInterface)this);
  }


}
