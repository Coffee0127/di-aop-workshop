package io.github.coffee0127.diaop.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AuthenticationServiceTest {

  @Test
  void is_valid() {
    var authenticationService = new AuthenticationService();
    var account = "joey";
    var password = "abc";
    var otp = "123456";

    var isValid = authenticationService.verify(account, password, otp);

    assertTrue(isValid);
  }
}
