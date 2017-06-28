package com.flt.sampleservice;

import com.flt.servicelib.AbstractBootReceiver;

public class DemonstrationBootReceiver extends AbstractBootReceiver<DemonstrationBindingService> {

  @Override
  protected Class<DemonstrationBindingService> getServiceClass() {
    return DemonstrationBindingService.class;
  }

}
