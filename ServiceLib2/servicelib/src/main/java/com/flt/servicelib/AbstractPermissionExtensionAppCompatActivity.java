package com.flt.servicelib;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * <p>
 * The <b>getRequiredPermissions</b> method returns a String[] of permissions this app requires.
 * These will be automatically requested when you call <b>requestAllPermissions</b>. You can check
 * on the state of permissions with <b>anyOutstandingPermissions</b> and <b>needsPermission</b>.
 * Available Android permissions can be found in: Manifest.permission
 * </p>
 * <p>
 * <b>NB. All permissions requested by any part of the app must also be listed in the app's manifest,
 * with uses-permission tags.</b>
 * </p>
 * <p>
 * The Overlay permission is special, different, and contentious! Right now it is granted automatically
 * to apps installed from the Play Store because <i>reasons</i>. That may be changing with Android O,
 * and it's possible a new method for requesting that permission will come into play. In the meantime,
 * you can use <b>hasOverlayPermission</b> and <b>requestOverlayPermission</b> should you need to.
 * I recommend checking and not assuming you have it (if you need it), as the rules are predicted to
 * change.
 * </p>
 * <p>
 * There are various helper methods in this class, available to your inheriting classes. <b>informUser</b>
 * will display a Toast to the user, and is safe to call from any thread. <b>setTitleBarToVersionWith</b>
 * helpfully sets the Activity's titlebar to display the title provided and suffix it with the app's
 * version name.
 * </p>
 */
public abstract class AbstractPermissionExtensionAppCompatActivity extends AppCompatActivity {
  private static final String TAG = "PermissionExtActivity";

  protected static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 7001;
  protected static final int REQUEST_ALL_PERMISSION_CODES = 7002;

  /**
   * Helper method to extract the version name of this app, append it to the title provided, and
   * set this as the Activity's title bar text.
   * @param title the text of the title before the version number
   */
  protected void setTitleBarToVersionWith(String title) {
    try {
      PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
      String version = pInfo.versionName;
      getSupportActionBar().setTitle(title + " " + version);
    } catch (PackageManager.NameNotFoundException e) {
      Log.w(TAG, "Exception encountered reading version number.", e);
    }
  }

  public boolean hasOverlayPermission() {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this);
  }

  public void requestOverlayPermission() {
    if (!hasOverlayPermission()) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
      startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
    } else {
      onUnecessaryCallToRequestOverlayPermission();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
        if (Settings.canDrawOverlays(this)) {
          // You have permission
          onGrantedOverlayPermission();
        } else {
          onRefusedOverlayPermission();
        }
      }

    } else {
      // not android M+ -- permissions assumed granted
      if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
        onGrantedOverlayPermission();
      }
    }
  }

  /**
   * @return true if any permission from getRequiredPermissions is not granted.
   */
  protected boolean anyOutstandingPermissions() {
    for (int i = 0; i < getRequiredPermissions().length; i++) {
      if (needsPermission(getRequiredPermissions()[i])) { return true; }
    }
    return false;
  }

  /**
   * Invokes the UI to request all permissions as provided by getRequiredPermissions.
   */
  protected void requestAllPermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requestPermissions(getRequiredPermissions(), REQUEST_ALL_PERMISSION_CODES);
    } else {
      onPermissionsGranted(); // automatically granted before M
    }
  }

  /**
   * Checks for the given permission.
   * @param permission a permission (Android permissions are available in Manifest.permissions)
   * @return true if this permission is still required
   */
  protected boolean needsPermission(String permission) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED;
    } else {
      return false; // permission automatically granted before Marshmallow
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch (requestCode) {
      case REQUEST_ALL_PERMISSION_CODES:
        boolean aok = true;
        for (int i = 0; i < grantResults.length; i++) {
          if (grantResults[i] != PackageManager.PERMISSION_GRANTED) { aok = false; }
        }
        if (aok) {
          onPermissionsGranted();
        } else {
          onNotAllPermissionsGranted();
        }

        break;
    }
  }

  /**
   * Implement behaviours for when the Overlay permission has been granted.
   */
  protected abstract void onGrantedOverlayPermission();

  /**
   * Implement behaviours (such as complaints or warnings) if the Overlay permission is refused.
   */
  protected abstract void onRefusedOverlayPermission();

  /**
   * Provide all required permissions for this application/activity as a String[] here.
   * Android permissions are available from Manifest.permission.
   */
  protected abstract String[] getRequiredPermissions();

  /**
   * Implement behaviours for when all permissions have been granted.
   */
  protected abstract void onPermissionsGranted();

  /**
   * Implement behaviours for when not all permissions were granted.
   */
  protected abstract void onNotAllPermissionsGranted();

  /**
   * An unnecessary request was made to obtain overlay permissions. Usually, just ignore this.
   */
  protected abstract void onUnecessaryCallToRequestOverlayPermission();


  /**
   * Helper method to display a Toast for the user, safely from any thread.
   */
  protected void informUser(String msg) {
    final String finalMsg = msg;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(AbstractPermissionExtensionAppCompatActivity.this, finalMsg, LENGTH_SHORT).show();
      }
    });
  }

  protected void informUser(int string_resource) {
    informUser(getString(string_resource));
  }

}
