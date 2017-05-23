# background-service-lib
Essential classes for reliable background Services.

## What is servicelib?

Servicelib provide you with a number of classes to support you to quickly build a Service into your app that runs persistently in the background.

Android can destroy any Service or Activity at any time to relieve memory constraints - but this Service will hint strongly that the user has an interest in it running uninterrupted. If the Service is halted due to memory constraints, or any other reason, it will restart as soon as conditions become favourable again. The Service will run if any activities are bound to it or not, and will start on boot.

Servicelib also provides you with an abstract Activity class that automatically binds to the Service whenever it is in use, and unbinds when it is not. This gives you access to the Service from your Activity as if it were just another POJO.

Your Activities are also able to request permissions for your app, and helper methods can help you to determine if all the required permissions are granted.

_Nothing is going to make Android app Services simple or easy - but this library will help you cut out a lot of the routine boilerplate code._

## Installation

To import this project using gradle, add the following to your dependencies:

    compile 'com.github.instantiator:background-service-lib:1.0'

Also, ensure that there's an entry for `jcenter()` listed somewhere, eg. in the root `build.gradle` for your Project:

    allprojects {
        repositories {
            jcenter()
        }
    }

You are welcome to clone and fork this repository to your heart's content.

## How do I use it?

In order to build a reliable Service, there are a few things you must do. Servicelib can only go so far...

* Create an interface for your Service - with all the methods you expect any bound activities to call.
* Subclass the AbstractBackgroundService, and implement all your Service interface methods.
* Provide the AbstractBackgroundService with a basic configuration.
* Subclass the AbstractBootReceiver, and register it in your manifest file to receive the BOOT_COMPLETED intent.
* Override the default Application class, and use it to start the Service when the Application starts.
* Set this Application class as the android:name for your Application in the manifest file.
* Build an Activity that subclasses AbstractServiceBoundAppCompatActivity, and implement the abstract methods for the permission-related events.
* Ensure that the required permissions specified in getRequiredPermissions in your activities and Service are also present in the manifest file.
* Test!

All of these steps are demonstrated in the accompanying app - which serves to demonstrate a working, reliable Service (and a bound Activity that can call methods on that Service).

### How do I set up my Service?

Subclass the AbstractBackgroundService and provide it with details of the interface it implements:

    public class DemonstrationService extends AbstractBackgroundService<DemonstrationServiceInterface> implements DemonstrationServiceInterface {

There are a small number of abstract methods to implement - each of which handles a specific permissions-related event:

* __configure__ - Allows you to configure the Service, providing it with details to build a notification.
* __getRequiredPermissions__ - Allows you to specify the permissions this Service can check for.

The other methods you should provide are those from the Service's interface - which can then be called by any bound Activities.

### Why do I need a notification for my Service?

An Android Service can use a notification to become a _Foreground Service_. In the foreground, Android considers that the user has an interest in the service - and so prioritises it very highly. As a result, the Service is far less likely to be destroyed due to memory constraints until it really has to be.

Of course, Android reserves the right to destroy any Service or Activity, so you must still write resilient code, but this increases the likelihood that the Service will run for as much time as possible - and allows you to provide a visual indicator to your user to reassure them that the Service is running.

#### How do I configure the notification in my Service?

Configure your Service's notification using the __configure__ method in your Service class. You may make modifications to (or provide an entirely new) __BackgroundServiceConfig__ object and return the result:

    @Override
    protected BackgroundServiceConfig configure(BackgroundServiceConfig config) {
      config.setNotification(
          getString(R.string.service_notification_title),
          getString(R.string.service_notification_content),
          getString(R.string.service_notification_ticker),
          R.mipmap.ic_service,
          MainActivity.class);
    
      return config;
    }

### What if my service is stopped by Android?

The purpose of this library is to help you build a Service that Android will try to preserve - however, when memory is tight and all else has failed, your service can still be stop until conditions are favourable again.

In this case, your Service should stop gracefully - and record its state (in the DefaultSettings perhaps). It follows then, that your Service should also check for and learn its stored state when it resumes - so as to be able to pick up where it left off. At current time the library does not help you to do this - but there are some additional helper methods planned to help you write your code for this eventuality.

### Why do I need to subclass Application?

When an Activity unbinds from a Service, the Service checks how many bindings it has. If those bindings have dropped to zero _and the Service was not started by any other means_ then the Service is stopped. If you have already started the Service separately - ie. through the Application object (or the BOOT_COMPLETED BroadcastReceiver), then it will continue to run.

We override the Application class as it is the perfect place to ensure that the Service is started without a binding, no matter which Activity the user goes to after that.

    public class DemonstrationApp extends Application {
      @Override
      public void onCreate() {
        super.onCreate();
        Intent i = new Intent(this, DemonstrationService.class);
        startService(i);
      }
    
    }

### Tell me some more about AbstractServiceBoundAppCompatActivity

The AbstractServiceBoundAppCompatActivity class attempts to bind to your Service as soon as it is in use, and unbinds whenever it is not.

The Activity also provides a number of methods and member variables of note:

* You can test to see if the activity has succesfully bound to your Service with the boolean __bound__ member variable.
* Implement the __onBoundChanged__ method to update your UI and hide or show features based on whether the Service is available.
* Make use of the __requestAllPermissions__ method to request permissions from your user.
* Define the permissions you need by implementing the __String[] getRequiredPermissions__ method.
* Two methods will be called for events related to permissions: __onPermissionsGranted__, __onNotAllPermissionsGranted__.
* A helper method called __informUser__ creates a Toast for the user safely (even if not called from the UI thread).
* A helper method called __setTitleBarToVersionWith__ can update the title bar of the Activity with the version name of the app.

Remember that all permissions requested by any part of the app must also be listed in the app's manifest, with uses-permission tags.

### What's all this about permissions?

Your Service won't be able to ask for its own permissions - so you'll need help from an Activity to get these granted by the user. The Android methods for requesting permissions are ok, but the detail is a pain to have to implement over and over. The AbstractServiceBoundAppCompatActivity has the capability to request permissions from the user, and will trigger one or more of the (relatively simple) abstract event methods on completion of this task.

### How do I request permissions?

In your subclass of AbstractServiceBoundAppCompatActivity, implement __getRequiredPermissions__ to provide a String[] of permissions to request, and then from your UI code make a call to __requestAllPermissions__:

    @Override
    protected String[] getRequiredPermissions() {
      return new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE };
    }

    btn_get_permissions.setOnClickListener(new View.OnClickListener() {
      @your
      public void onClick(View v) {
        requestAllPermissions();
      }
    });

The outcome will result in a call to one of the following abstract methods:

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

Make sure that your manifest file contains the same permissions as __uses-permission__ tags:

    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

### What about those overlay permission methods?

The overlay permission is special, different, and contentious! (Not least because the ability to draw over any other apps carries with it significant risk of abuse.) Right now it is granted automatically to apps installed from the Play Store because _reasons_. That may be changing with Android O, and it's possible a new method for requesting that permission will come into play. In the meantime, you can use __hasOverlayPermission__ and __requestOverlayPermission__ should you need to.

I recommend checking and not assuming you have the overlay permission it (if you need it), as the rules are predicted to change.

You will also need a line in the manifest file to request this permission, which is unusually named:

    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>

### How do I ensure my Service starts when the phone is powered on?

This is where you need a __BroadcastReceiver__. Subclass the AbstractBootReceiver, and implement the __getServiceClass__ method to provide it with the class for your Service:

    public class DemonstrationBootReceiver extends AbstractBootReceiver<DemonstrationService> {
      @Override
      protected Class<DemonstrationService> getServiceClass() {
        return DemonstrationService.class;
      }
    }

Your receiver will also need an entry in the manifest file, with an intent filter to ensure it receives the BOOT_COMPLETED intent:

        <receiver
            android:name=".DemonstrationBootReceiver"
            android:enabled="true"
            android:exported="true">

            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>

        </receiver>

### Credits

* See: https://stfalcon.com/en/blog/post/create-and-publish-your-Android-library for a comprehensive guide to publishing Android libraries to jCenter.
* See also: https://medium.com/@daniellevass/how-to-publish-your-android-studio-library-to-jcenter-5384172c4739

## Licensing

Do as you please - commercially or otherwise, but if you significantly improve this project, I'd invite you to let me know what you did through a comment or a pull request. Thanks!
