package io.github.coffee0127.diaop.service;

import io.github.coffee0127.diaop.gateway.Notification;

public class NotificationDecorator extends AuthDecoratorBase {

  private final Notification notification;

  public NotificationDecorator(IAuth auth, Notification notification) {
    super(auth);
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
