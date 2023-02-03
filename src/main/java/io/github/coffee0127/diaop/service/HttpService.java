package io.github.coffee0127.diaop.service;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

public class HttpService {

  private final HttpClient httpClient;

  public HttpService() {
    httpClient = HttpClients.createDefault();
  }

  public String get(String url) {
    var request = new HttpGet(url);
    try {
      return httpClient.execute(request, response -> EntityUtils.toString(response.getEntity()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
