package com.flt.sampleservice;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.flt.servicelib.AbstractServiceBoundAppCompatActivity;

public class MainActivity extends AbstractServiceBoundAppCompatActivity<DemonstrationService, DemonstrationServiceInterface> {

  Button btn_get_permissions;
  Button btn_do_something;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    btn_get_permissions = (Button)findViewById(R.id.btn_get_permissions);
    btn_do_something = (Button)findViewById(R.id.btn_do_something);

    btn_get_permissions.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        requestAllPermissions();
      }
    });

    btn_do_something.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        attemptToDoSomething();
      }
    });

    setTitleBarToVersionWith(getString(R.string.title_main_activity));
  }

  private void attemptToDoSomething() {
    if (!bound) {
      informUser(R.string.toast_activity_cannot_service_unavailable);
      return;
    }

    if (anyOutstandingPermissions()) {
      informUser(R.string.toast_activity_cannot_do_something);
    } else {
      String message = service.doSomething();
      informUser(message);
    }
  }

  @Override
  protected Class getServiceClass() {
    return DemonstrationService.class;
  }

  @Override
  protected void onBoundChanged(boolean isBound) {
    updateUI();
  }

  private void updateUI() {
    btn_get_permissions.setEnabled(bound && anyOutstandingPermissions());
    btn_do_something.setEnabled(bound && !anyOutstandingPermissions());
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, R.string.menu_about, 10, getString(R.string.menu_about));
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.string.menu_about:
        showAbout();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void showAbout() {
    new AlertDialog.Builder(this)
        .setTitle(R.string.title_about)
        .setMessage(R.string.text_about)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .show();
  }
}
