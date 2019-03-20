package com.flt.servicelib;

import android.app.Activity;
import android.app.Notification;

public class BackgroundServiceConfig {

  public String notification_title;
  public String notification_content;
  public String notification_ticker;
  public int notification_icon;
  public int notification_priority; // 7.1 or lower
  public Class<Activity> notification_activity_class;
  public boolean show_notification;

  public String notification_channel_name;
  public String notification_channel_description;
  public int notification_channel_importance; // 8.0 or higher

  public BackgroundServiceConfig() {
    show_notification = false;
  }

  /**
   * Use this method to apply configuration that enables an ongoing foreground service notification.
   * For details of notification priority and importance, see:
   * https://developer.android.com/training/notify-user/channels#importance
   *
   * @param title the title of the notification
   * @param content the text of the notification
   * @param ticker text to appear in the ticker when the notification is created
   * @param icon the icon for this notification
   * @param activity_class class of an activity to launch when the notification is tapped
   * @param channel_name the title of the notification channel
   * @param channel_description a description of the notification channel
   * @param channel_importance the importance of this channel (Android 8.0 and above)
   * @param notification_priority the priority for the notification (Android 7.1 and below)
   */
  public void setNotification(String title, String content, String ticker, int icon, Class activity_class, String channel_name, String channel_description, int channel_importance, int notification_priority) {
    this.show_notification = true;
    this.notification_title = title;
    this.notification_content = content;
    this.notification_ticker = ticker;
    this.notification_icon = icon;
    this.notification_activity_class = activity_class;
    this.notification_channel_name = channel_name;
    this.notification_channel_description = channel_description;
    this.notification_channel_importance = channel_importance;
    this.notification_priority = notification_priority;
  }

}
