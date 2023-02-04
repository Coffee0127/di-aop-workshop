package io.github.coffee0127.diaop.service;

public class FailCounter {

  private final HttpService httpService;

  public FailCounter() {
    httpService = new HttpService();
  }

  public boolean isLocked(String account) {
    return Boolean.parseBoolean(
        httpService.get("https://my-api.com/api/failedCounter/isLocked?account=" + account));
  }

  public void reset(String account) {
    httpService.post("https://my-api.com/api/failedCounter/reset?account=" + account);
  }

  public void add(String account) {
    httpService.post("https://my-api.com/api/failedCounter/add?account=" + account);
  }

  String get(String account) {
    return httpService.post(
        "https://my-api.com/api/failedCounter/getFailedCount?account=" + account);
  }
}
