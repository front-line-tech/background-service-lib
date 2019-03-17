package com.flt.servicelib;

import android.os.Binder;

public class ServiceBinder<ServiceInterface> extends Binder implements IServiceBinder<ServiceInterface> {

  ServiceInterface service;

  protected ServiceBinder(ServiceInterface service) {
    this.service = service;
  }

  @Override
  public ServiceInterface getService() { return service; }
}
