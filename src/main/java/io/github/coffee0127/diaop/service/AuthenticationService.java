package io.github.coffee0127.diaop.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationService {

  private final IProfileRepo profileRepo;
  private final IHash hash;
  private final IOtp otp;
  private final Notification notification;
  private final IFailCounter failCounter;
  private final MyLogger myLogger;

  public AuthenticationService() {
    profileRepo = new ProfileRepo();
    hash = new Sha256Adapter();
    otp = new OtpAdapter();
    notification = new SlackAdapter();
    failCounter = new FailCounter();
    myLogger = new Slf4JLogAdapter();
  }

  public boolean verify(String account, String password, String otp) {
    var httpService = new HttpService();

    var isLocked = failCounter.isLocked(account);
    if (isLocked) {
      throw new FailedTooManyTimesException(account);
    }
    var passwordFromDb = profileRepo.getPassword(account);

    var hashedPassword = hash.getHashedResult(password);

    var currentOtp = this.otp.getCurrentOtp(account, httpService);

    if (passwordFromDb.equals(hashedPassword) && currentOtp.equals(otp)) {
      failCounter.reset(account);
      return true;
    } else {
      failCounter.add(account);

      logFailedCount(account);

      var message = "Account: " + account + " try to login failed";
      notification.notify(message);
      return false;
    }
  }

  private void logFailedCount(String account) {
    var failedCount = failCounter.get(account);
    myLogger.info("accountId:" + account + " failed times:" + failedCount);
  }
}
