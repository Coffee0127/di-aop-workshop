package io.github.coffee0127.diaop.service;

import io.github.coffee0127.diaop.gateway.Notification;

public class NotificationDecorator implements IAuth {

  private final IAuth auth;
  private final Notification notification;

  public NotificationDecorator(IAuth auth, Notification notification) {
    this.auth = auth;
    this.notification = notification;
  }

  private void notifyUser(String account) {
    var message = "account:" + account + " try to login failed";
    notification.notify(message);
  }

  @Override
  public boolean verify(String account, String password, String otp) {
    var isValid = auth.verify(account, password, otp);
    if (!isValid) {
      notifyUser(account);
    }
    return isValid;
  }
}
