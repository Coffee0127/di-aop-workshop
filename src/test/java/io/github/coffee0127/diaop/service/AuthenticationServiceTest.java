package io.github.coffee0127.diaop.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
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
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthenticationServiceTest {

  private IProfileRepo profileRepo;
  private Notification notification;
  private IHash hash;
  private IOtp otp;
  private IFailCounter failCounter;
  private MyLogger myLogger;
  private IAuth auth;

  @BeforeEach
  void setUp() {
    profileRepo = mock(IProfileRepo.class);
    hash = mock(IHash.class);
    otp = mock(IOtp.class);
    failCounter = mock(IFailCounter.class);
    myLogger = mock(MyLogger.class);

    auth = new AuthenticationService(profileRepo, hash, otp, failCounter, myLogger);

    notification = mock(Notification.class);
    auth = new NotificationDecorator(auth, notification);

    auth = new FailCounterDecorator(auth, failCounter);
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

    auth.verify("joey", "abc", "123456");

    verify(failCounter, times(1)).reset("joey");
  }

  @Test
  void should_notify_user_when_invalid() {
    whenInvalid("joey");

    shouldNotify("joey", "failed");
  }

  @Test
  void should_add_failed_count_when_invalid() {
    whenInvalid("joey");

    shouldAddFailedCount("joey");
  }

  @Test
  void should_log_failed_count_when_invalid() {
    givenLatestFailedCount("joey", 3);

    whenInvalid("joey");

    shouldLog("3");
  }

  private void shouldLog(String... keywords) {
    verify(myLogger, times(1)).info(containsStrings(keywords));
  }

  private String containsStrings(String[] keywords) {
    return argThat(message -> Arrays.stream(keywords).allMatch(message::contains));
  }

  private void givenLatestFailedCount(String account, int failedCount) {
    when(failCounter.get(account)).thenReturn(failedCount);
  }

  private void shouldAddFailedCount(String account) {
    verify(failCounter, times(1)).add(account);
  }

  private void whenInvalid(String account) {
    givenAccountIsLocked(account, false);
    givenPasswordFromDb(account, "ABC123");
    givenHashedResult("abc", "wrong password hashed result");
    givenCurrentOtp(account, "123456");

    auth.verify(account, "abc", "123456");
  }

  private void shouldNotify(String... keywords) {
    verify(notification, times(1)).notify(containsStrings(keywords));
  }

  private <T extends Throwable> void shouldThrow(
      Class<T> expectedType, String account, String password, String otp) {
    assertThrows(expectedType, () -> auth.verify(account, password, otp));
  }

  private void shouldBeInValid(String account, String password, String otp) {
    var isValid = auth.verify(account, password, otp);
    assertFalse(isValid);
  }

  private void shouldBeValid(String account, String password, String otp) {
    var isValid = auth.verify(account, password, otp);
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
