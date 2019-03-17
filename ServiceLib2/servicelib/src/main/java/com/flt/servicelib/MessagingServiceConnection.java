package com.flt.servicelib;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

/**
 * <p>
 * A class for establishing a 1-way link with a service running in another process. This link uses
 * a Messenger object to transmit message bundles. To observe change in state, implement and set the
 * MessagingServiceConnection.Listener.
 * </p>
 *
 * <p>
 * If the service this class connects to supports it, you can establish 2-way communication, as the
 * MessagingServiceConnection implements a second Messenger with Handler for receiving messages by
 * way of return (and includes that Messenger in the replyTo field of all Messages that it sends).
 * </p>
 *
 * <p>
 * Implement the onMessageReceived method of the listener to receive messages sent to this connection.
 * </p>
 */
public class MessagingServiceConnection implements ServiceConnection {
  private boolean bound;

  private Messenger transmitter;
  private Messenger receiver;

  private Listener listener;

  public void send(int message, Bundle bundle) throws Exception {
    if (!bound) { throw new IllegalStateException("Not yet bound."); }
    Message msg = Message.obtain(null, message);
    msg.setData(bundle);
    msg.replyTo = receiver;
    transmitter.send(msg);
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
    this.transmitter = new Messenger(binder);
    this.receiver = new Messenger(new IncomingHandler());
    if (listener != null) {
      listener.onConnected(this);
    }
  }

  @Override
  public void onServiceDisconnected(ComponentName name) {
    // unbind or process might have crashes
    transmitter = null;
    receiver = null;
    bound = false;
    if (listener != null) {
      listener.onDisconnected(this);
    }
  }

  public interface Listener {
    void onConnected(MessagingServiceConnection source);
    void onDisconnected(MessagingServiceConnection source);
    void onMessageReceived(Message message);
  }

  public class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      if (listener != null) {
        listener.onMessageReceived(msg);
      }
    }
  }
}
