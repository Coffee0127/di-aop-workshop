package io.github.coffee0127.diaop.service;

public class FailCounter implements IFailCounter {

  private final HttpService httpService;

  public FailCounter() {
    httpService = new HttpService();
  }

  @Override
  public boolean isLocked(String account) {
    return Boolean.parseBoolean(
        httpService.get("https://my-api.com/api/failedCounter/isLocked?account=" + account));
  }

  @Override
  public void reset(String account) {
    httpService.post("https://my-api.com/api/failedCounter/reset?account=" + account);
  }

  @Override
  public void add(String account) {
    httpService.post("https://my-api.com/api/failedCounter/add?account=" + account);
  }

  @Override
  public String get(String account) {
    return httpService.post(
        "https://my-api.com/api/failedCounter/getFailedCount?account=" + account);
  }
}
