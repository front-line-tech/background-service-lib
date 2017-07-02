package com.flt.servicelib;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

/**
 * A class for establishing a 1-way link with a service running in another process. This link uses
 * a Messenger object to transmit message bundles. To observe change in state, implement and set the
 * MessagingServiceConnection.Listener.
 */
public class MessagingServiceConnection implements ServiceConnection {
  private boolean bound;
  private Messenger messenger;
  private MessagingServiceConnection.Listener listener;

  public void send(int message, Bundle bundle) throws Exception {
    if (!bound) { throw new IllegalStateException("Not yet bound."); }

    Message msg = Message.obtain(null, message);
    msg.setData(bundle);
    messenger.send(msg);
  }

  public boolean bound() {
    return bound;
  }

  public Listener getListener() {
    return listener;
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  @Override
  public void onServiceConnected(ComponentName name, IBinder binder) {
    bound = true;
    this.messenger = new Messenger(binder);
    if (listener != null) {
      listener.onConnected(this);
    }
  }

  @Override
  public void onServiceDisconnected(ComponentName name) {
    // unbind or process might have crashes
    messenger = null;
    bound = false;
    if (listener != null) {
      listener.onDisconnected(this);
    }
  }

  public interface Listener {
    void onConnected(MessagingServiceConnection source);
    void onDisconnected(MessagingServiceConnection source);
  }
}
