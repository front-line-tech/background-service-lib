package com.flt.servicelib;

import android.os.IBinder;

public abstract class AbstractBackgroundBindingService<ServiceInterface> extends AbstractBackgroundService {

  @Override
  protected IBinder createBinder() {
    return new ServiceBinder<ServiceInterface>((ServiceInterface)this);
  }


}
