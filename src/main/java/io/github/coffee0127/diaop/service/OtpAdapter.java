package io.github.coffee0127.diaop.service;

public class OtpAdapter implements IOtp {

  @Override
  public String getCurrentOtp(String account, HttpService httpService) {
    return httpService.get("https://my-api.com/otp?account=" + account);
  }
}
