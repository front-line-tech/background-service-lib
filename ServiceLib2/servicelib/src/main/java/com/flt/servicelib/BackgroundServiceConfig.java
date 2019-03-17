package com.flt.servicelib;

import android.app.Activity;
import android.app.Notification;

public class BackgroundServiceConfig {

  public String notification_title;
  public String notification_content;
  public String notification_ticker;
  public int notification_icon;
  public int notification_priority;
  public Class<Activity> notification_activity_class;
  public boolean show_notification;

  public String notification_channel_name;
  public String notification_channel_description;
  public int notification_channel_importance;

  public BackgroundServiceConfig() {
    show_notification = false;
  }

  public void setNotification(String title, String content, String ticker, int icon, Class activity_class, String channel_name, String channel_description, int channel_importance) {
    show_notification = true;
    notification_title = title;
    notification_content = content;
    notification_ticker = ticker;
    notification_icon = icon;
    notification_activity_class = activity_class;
    notification_priority = Notification.PRIORITY_MAX;

    notification_channel_name = channel_name;
    notification_channel_description = channel_description;
    notification_channel_importance = channel_importance;
  }

}
