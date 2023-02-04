package io.github.coffee0127.diaop.gateway;

import com.example.external.HttpClient;

public class OtpAdapter implements IOtp {

  public OtpAdapter() {}

  @Override
  public String getCurrentOtp(String account) {
    var response =
        new HttpClient("https://joey.com").get("/api/otps?account" + account, String.class);
    if (response.isSuccessStatusCode()) {
    } else {
      throw new RuntimeException("web api error, accountId:" + account);
    }
    return response.read();
  }
}
