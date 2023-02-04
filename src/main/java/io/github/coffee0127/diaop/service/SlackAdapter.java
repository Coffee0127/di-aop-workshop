package io.github.coffee0127.diaop.service;

public class SlackAdapter {

  public void notify(String message) {
    new SlackClient().postMessage(message);
  }
}
