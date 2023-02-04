package io.github.coffee0127.diaop.service;

public interface IOtp {

  String getCurrentOtp(String account, HttpService httpService);
}
