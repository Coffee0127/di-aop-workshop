package io.github.coffee0127.diaop.gateway;

import com.example.external.HttpClient;

public class OtpAdapter {

  public OtpAdapter() {}

  public String getCurrentOtp(String account, HttpClient httpClient) {
    var response = httpClient.get("/api/otps?account" + account, String.class);
    if (response.isSuccessStatusCode()) {
    } else {
      throw new RuntimeException("web api error, accountId:" + account);
    }
    return response.read();
  }
}
