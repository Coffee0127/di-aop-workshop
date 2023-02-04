package com.example.external;

public class SlackClient {

  private final String token;

  public SlackClient(String token) {
    this.token = token;
  }

  public void postMessage(String channel, String message) {
    System.out.println("Send message to " + channel);
  }
}
