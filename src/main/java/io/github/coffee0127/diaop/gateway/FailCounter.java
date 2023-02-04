package io.github.coffee0127.diaop.gateway;

import com.example.external.HttpClient;

public class FailCounter implements IFailCounter {

  private final HttpClient httpClient;

  public FailCounter() {
    httpClient = new HttpClient("https://joey.com");
  }

  @Override
  public boolean isLocked(String account) {
    var isLockedResponse =
        httpClient.get("/api/failedCounter/isLocked?account=" + account, Boolean.class);
    isLockedResponse.ensureSuccessStatusCode();
    return isLockedResponse.read();
  }

  @Override
  public void reset(String account) {
    var resetResponse = httpClient.post("/api/failedCounter/reset?account=" + account, Void.class);
    resetResponse.ensureSuccessStatusCode();
  }

  @Override
  public void add(String account) {
    var addFailedCountResponse =
        httpClient.post("/api/failedCounter/add?account=" + account, Void.class);
    addFailedCountResponse.ensureSuccessStatusCode();
  }

  @Override
  public Integer get(String account) {
    var failedCountResponse =
        httpClient.post("/api/failedCounter/getFailedCount?account=" + account, Integer.class);
    failedCountResponse.ensureSuccessStatusCode();
    return failedCountResponse.read();
  }
}
