package com.flt.servicelib;

import android.os.IBinder;

public interface IServiceBinder<ServiceInterface> extends IBinder {
  ServiceInterface getService();
}
