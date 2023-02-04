package io.github.coffee0127.diaop.gateway;

import com.example.external.HttpClient;

public class FailCounter {

  public FailCounter() {}

  public void reset(String account) {
    var resetResponse =
        new HttpClient("https://joey.com")
            .post("/api/failedCounter/reset?account=" + account, Void.class);
    resetResponse.ensureSuccessStatusCode();
  }
}
