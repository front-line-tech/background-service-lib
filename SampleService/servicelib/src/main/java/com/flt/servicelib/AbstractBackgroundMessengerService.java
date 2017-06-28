package com.flt.servicelib;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;

public abstract class AbstractBackgroundMessengerService extends AbstractBackgroundService {

  protected Messenger messenger;
  protected Handler handler;

  @Override
  public void onCreate() {
    handler = createMessageHandler();
    messenger = new Messenger(handler);
    super.onCreate();
  }

  @Override
  protected IBinder createBinder() {
    return messenger.getBinder();
  }

  protected abstract Handler createMessageHandler();
}
