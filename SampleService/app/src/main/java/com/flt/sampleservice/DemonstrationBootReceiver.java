package com.flt.sampleservice;

import com.flt.servicelib.AbstractBootReceiver;

public class DemonstrationBootReceiver extends AbstractBootReceiver<DemonstrationService> {

  @Override
  protected Class<DemonstrationService> getServiceClass() {
    return DemonstrationService.class;
  }

}
