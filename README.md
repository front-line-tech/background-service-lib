# background-service-lib
Essential classes for reliable background services.

## What is servicelib?

Servicelib provide you with a number of classes to support you in quickly and easily build a service into your app that runs persistently in the background. Android can destroy any service or activity at any time to relieve memory constraints - but this service will hint strongly that the user has an interest in it running uninterrupted, and requests to Android that it restart as soon as memory becomes available again. The service will run if any activities are bound to it or not, and will start on boot.

Servicelib also provides you with an abstract Activity class that automatically binds to the service whenever it is in use, and unbinds when it is not. This gives you access to the service as if it were just another POJO. This activity is also able to request permissions for your app, and provides helper methods to determine if all the required permissions are granted.

Nothing is going to make Android app services simple or easy - but this library will help you cut out a lot of the routine boilerplate code.

## How do I use it?

In order to build a reliable service, there are a few things you must do. Servicelib can only go so far...

* Create an interface for your service - with all the methods you expect any bound activities to call.
* Subclass the AbstractBackgroundService, and implement all your service interface methods.
* Provide the AbstractBackgroundService with a basic configuration.
* Subclass the AbstractBootReceiver, and register it in your manifest file to receive the BOOT_COMPLETED intent.
* Override the default Application class, and use it to start the service when the Application starts.
* Set this Application class as the android:name for your Application in the manifest file.
* Build an Activity that subclasses AbstractServiceBoundAppCompatActivity, and implement the abstract methods for the permission-related events.
* Ensure that the required permissions specified in getRequiredPermissions in your activities and service are also present in the manifest file.
* Test!

All of these steps are demonstrated in the accompanying app - which serves to demonstrate a working, reliable service (and a bound activity that can call methods on that service).

### Why do I need a notification for my service?

An Android Service can use a notification to become a foreground service. In the foreground, Android considers that the user has an interest in the service - and so prioritises it very highly. As a result, the service is far less likely to be destroyed due to memory constraints until it really has to be.

Of course, Android reserves the right to destroy any service or activity, so you must still write resilient code, but this increases the likelihood that the service will run for as much time as possible - and allows you to provide a visual indicator to your user to reassure them that the service is running.

### What if my service is stopped by Android?

There are still opportunities for your service to stop gracefully - and record its state (in the DefaultSettings perhaps). In this case, it is also important that your service check for and learn its stored state when it resumes. At current time the library does not support this - but there are some additional helper methods planned to help you write your code for this eventuality.

### Why do I need to subclass Application?

When an activity unbinds from a service, the service checks how many bindings it has. If those bindings have dropped to zero _and the service was not started by any other means_ then the service is stopped. If you have already started the service separately - ie. through the Application object (or the BOOT_COMPLETED BroadcastReceiver), then it will continue to run.

We override the Application class as it is the perfect place to ensure that the service is started without a binding, no matter which activity the user goes to after that.

### What's all this about permissions?

Your service won't be able to ask for its own permissions - so you'll need help from an activity to get things moving! The Android methods for requesting permissions are ok, but the detail is a pain to have to implement over and over. The AbstractServiceBoundAppCompatActivity has the capability to request permissions from the user, and will trigger one or more of the (relatively simple) abstract event methods on completion of this task.

### Tell me some more about AbstractServiceBoundAppCompatActivity

The AbstractServiceBoundAppCompatActivity class attempst to bind to your service as soon as it is in use, and unbinds whenever it is not.

The activity also provides a number of methods and member variables of note:

* You can test to see if the activity has succesfully bound to your service with the boolean __bound__ member variable.
* Implement the __onBoundChanged__ method to update your UI and hide or show features based on whether the service is available.
* Make use of the __requestAllPermissions__ method to request permissions from your user.
* Define the permissions you need by implementing the __String[] getRequiredPermissions__ method.
* Two methods will be called for events related to permissions: __onPermissionsGranted__, __onNotAllPermissionsGranted__.
* A helper method called __informUser__ creates a Toast for the user safely (even if not called from the UI thread).
* A helper method called __setTitleBarToVersionWith__ can update the title bar of the activity with the version name of the app.

Remember that all permissions requested by any part of the app must also be listed in the app's manifest, with uses-permission tags.

### What about those overlay permission methods?

The overlay permission is special, different, and contentious! (Not least because the ability to draw over any other apps carries with it significant risk of abuse.) Right now it is granted automatically to apps installed from the Play Store because _reasons_. That may be changing with Android O, and it's possible a new method for requesting that permission will come into play. In the meantime, you can use __hasOverlayPermission__ and __requestOverlayPermission__ should you need to.

I recommend checking and not assuming you have the overlay permission it (if you need it), as the rules are predicted to change.
