package io.github.coffee0127.diaop.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.coffee0127.diaop.gateway.IFailCounter;
import io.github.coffee0127.diaop.gateway.IHash;
import io.github.coffee0127.diaop.gateway.IOtp;
import io.github.coffee0127.diaop.gateway.IProfileRepo;
import io.github.coffee0127.diaop.gateway.MyLogger;
import io.github.coffee0127.diaop.gateway.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthenticationServiceTest {

  private IProfileRepo profileRepo;
  private Notification notification;
  private IHash hash;
  private IOtp otp;
  private IFailCounter failCounter;
  private MyLogger myLogger;
  private AuthenticationService authenticationService;

  @BeforeEach
  void setUp() {
    profileRepo = mock(IProfileRepo.class);
    notification = mock(Notification.class);
    hash = mock(IHash.class);
    otp = mock(IOtp.class);
    failCounter = mock(IFailCounter.class);
    myLogger = mock(MyLogger.class);

    authenticationService =
        new AuthenticationService(profileRepo, notification, hash, otp, failCounter, myLogger);
  }

  @Test
  void is_valid() {
    givenAccountIsLocked("joey", false);
    givenPasswordFromDb("joey", "ABC123");
    givenHashedResult("abc", "ABC123");
    givenCurrentOtp("joey", "123456");

    shouldBeValid("joey", "abc", "123456");
  }

  @Test
  void invalid() {
    givenAccountIsLocked("joey", false);
    givenPasswordFromDb("joey", "ABC123");
    givenHashedResult("abc", "wrong password hashed result"); // hint: wrong password
    givenCurrentOtp("joey", "123456");

    shouldBeInValid("joey", "abc", "123456");
  }

  @Test
  void account_is_locked() {
    givenAccountIsLocked("joey", true); // hint: account is locked
    givenPasswordFromDb("joey", "ABC123");
    givenHashedResult("abc", "ABC123");
    givenCurrentOtp("joey", "123456");

    shouldThrow(FailedTooManyTimesException.class, "joey", "abc", "123456");
  }

  @Test
  void reset_failed_count_when_valid() {
    givenAccountIsLocked("joey", false);
    givenPasswordFromDb("joey", "ABC123");
    givenHashedResult("abc", "ABC123");
    givenCurrentOtp("joey", "123456");

    authenticationService.verify("joey", "abc", "123456");

    verify(failCounter, times(1)).reset("joey");
  }

  private <T extends Throwable> void shouldThrow(
      Class<T> expectedType, String account, String password, String otp) {
    assertThrows(expectedType, () -> authenticationService.verify(account, password, otp));
  }

  private void shouldBeInValid(String account, String password, String otp) {
    var isValid = authenticationService.verify(account, password, otp);
    assertFalse(isValid);
  }

  private void shouldBeValid(String account, String password, String otp) {
    var isValid = authenticationService.verify(account, password, otp);
    assertTrue(isValid);
  }

  private void givenAccountIsLocked(String account, boolean isLocked) {
    when(failCounter.isLocked(account)).thenReturn(isLocked);
  }

  private void givenPasswordFromDb(String account, String passwordFromDb) {
    when(profileRepo.getPassword(account)).thenReturn(passwordFromDb);
  }

  private void givenHashedResult(String password, String hashedResult) {
    when(hash.getHashedResult(password)).thenReturn(hashedResult);
  }

  private void givenCurrentOtp(String account, String currentOtp) {
    when(otp.getCurrentOtp(account)).thenReturn(currentOtp);
  }
}
