package io.github.coffee0127.diaop.gateway;

import com.example.external.HttpClient;

public class FailCounter {

  public FailCounter() {}

  public boolean isLocked(String account) {
    var isLockedResponse =
        new HttpClient("https://joey.com")
            .get("/api/failedCounter/isLocked?account=" + account, Boolean.class);
    isLockedResponse.ensureSuccessStatusCode();
    return isLockedResponse.read();
  }

  public void reset(String account) {
    var resetResponse =
        new HttpClient("https://joey.com")
            .post("/api/failedCounter/reset?account=" + account, Void.class);
    resetResponse.ensureSuccessStatusCode();
  }

  public void add(String account) {
    var addFailedCountResponse =
        new HttpClient("https://joey.com")
            .post("/api/failedCounter/add?account=" + account, Void.class);
    addFailedCountResponse.ensureSuccessStatusCode();
  }
}
