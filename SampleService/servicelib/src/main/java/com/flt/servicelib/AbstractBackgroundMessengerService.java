package com.flt.servicelib;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public abstract class AbstractBackgroundMessengerService extends AbstractBackgroundService {

  protected Messenger receiver;
  protected Handler handler;

  @Override
  public void onCreate() {
    handler = createMessageHandler();
    receiver = new Messenger(handler);
    super.onCreate();
  }

  @Override
  protected IBinder createBinder() {
    return receiver.getBinder();
  }

  protected Handler createMessageHandler() {
    return new Handler() {
      @Override
      public void handleMessage(Message msg) {
        onMessageReceived(msg);
      }
    };
  }

  protected abstract void onMessageReceived(Message message);

  protected void replyTo(Message original, Message with) throws RemoteException {
    if (original.replyTo != null) {
      original.replyTo.send(with);
    }
  }
}
