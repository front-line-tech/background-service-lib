package com.flt.servicelib;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class MessagingServiceConnection implements ServiceConnection {
  private boolean bound;
  private Messenger messenger;

  public void send(int message, Bundle bundle) throws Exception {
    if (!bound) { throw new IllegalStateException("Not yet bound."); }

    Message msg = Message.obtain(null, message);
    msg.setData(bundle);
    messenger.send(msg);
  }

  @Override
  public void onServiceConnected(ComponentName name, IBinder service) {
    bound = true;
    messenger = new Messenger(service);
  }

  @Override
  public void onServiceDisconnected(ComponentName name) {
    // unbind or process might have crashes
    messenger = null;
    bound = false;
  }
}
