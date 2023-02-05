package io.github.coffee0127.diaop.service;

import io.github.coffee0127.diaop.gateway.Notification;

public class NotificationDecorator {

  private final AuthenticationService authenticationService;
  private final Notification notification;

  public NotificationDecorator(
      AuthenticationService authenticationService, Notification notification) {
    this.authenticationService = authenticationService;
    this.notification = notification;
  }

  private void notifyUser(String account) {
    var message = "account:" + account + " try to login failed";
    notification.notify(message);
  }
}
