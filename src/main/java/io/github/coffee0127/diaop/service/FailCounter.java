package io.github.coffee0127.diaop.service;

public class FailCounter {

  public boolean isLocked(String account) {
    return Boolean.parseBoolean(
        new HttpService().get("https://my-api.com/api/failedCounter/isLocked?account=" + account));
  }

  public void reset(String account) {
    new HttpService().post("https://my-api.com/api/failedCounter/reset?account=" + account);
  }

  public void add(String account) {
    new HttpService().post("https://my-api.com/api/failedCounter/add?account=" + account);
  }
}
