package io.github.coffee0127.diaop.service;

public class SlackAdapter implements Notification {

  @Override
  public void notify(String message) {
    new SlackClient().postMessage(message);
  }
}
