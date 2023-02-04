package io.github.coffee0127.diaop.gateway;

import com.example.external.SlackClient;

public class SlackAdapter implements Notification {

  public SlackAdapter() {}

  @Override
  public void notify(String message) {
    var slackClient = new SlackClient("<YOUR_API_TOKEN>");
    slackClient.postMessage("#my-channel", message);
  }
}