package com.flt.sampleservice;

import android.Manifest;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.flt.servicelib.AbstractMessengerServiceBoundAppCompatActivity;

public class MessengerActivity extends AbstractMessengerServiceBoundAppCompatActivity {
  private static final String TAG = "MessengerActivity";

  private static int next_message = 333;

  Button btn_get_permissions;
  Button btn_send_message;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_messenger);

    setTitleBarToVersionWith(getString(R.string.title_messenger_activity));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    btn_get_permissions = (Button)findViewById(R.id.btn_get_permissions);
    btn_send_message = (Button)findViewById(R.id.btn_send_message);

    btn_get_permissions.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        requestAllPermissions();
      }
    });

    btn_send_message.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage();
      }
    });
  }

  private void sendMessage() {
    Bundle bundle = new Bundle();

    try {
      connection.send(next_message++, bundle);
    } catch (Exception e) {
      informUser("Unable to send message: " + e.getMessage());
      Log.e(TAG, "Unable to send message.", e);
    }

  }

  @Override
  protected ComponentName createServiceComponentName() {
    return new ComponentName("com.flt.sampleservice", "com.flt.sampleservice.DemonstrationMessagingService");
  }

  @Override
  protected void onResume() {
    super.onResume();
    updateUI();
  }

  @Override
  protected void onBoundChanged(boolean isBound) {
    updateUI();
  }

  @Override
  protected void onGrantedOverlayPermission() {
    informUser(R.string.toast_overlay_granted);
    updateUI();
  }

  @Override
  protected void onRefusedOverlayPermission() {
    informUser(R.string.toast_overlay_refused);
    updateUI();
  }

  @Override
  protected String[] getRequiredPermissions() {
    return new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE };
  }

  @Override
  protected void onPermissionsGranted() {
    informUser(R.string.toast_permissions_granted);
    updateUI();
  }

  @Override
  protected void onNotAllPermissionsGranted() {
    informUser(R.string.toast_permissions_not_granted);
    updateUI();
  }

  @Override
  protected void onUnecessaryCallToRequestOverlayPermission() {
    informUser(R.string.toast_unnecessary_overlay_request);
    updateUI();
  }

  private void updateUI() {
    btn_get_permissions.setEnabled(bound && anyOutstandingPermissions());
    btn_send_message.setEnabled(bound && !anyOutstandingPermissions());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }

  }
}
