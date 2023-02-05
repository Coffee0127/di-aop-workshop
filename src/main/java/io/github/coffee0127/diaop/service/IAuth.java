package io.github.coffee0127.diaop.service;

public interface IAuth {

  boolean verify(String account, String password, String otp);
}
