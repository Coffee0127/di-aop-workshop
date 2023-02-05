package io.github.coffee0127.diaop.service;

import io.github.coffee0127.diaop.gateway.IFailCounter;
import io.github.coffee0127.diaop.gateway.IHash;
import io.github.coffee0127.diaop.gateway.IOtp;
import io.github.coffee0127.diaop.gateway.IProfileRepo;
import io.github.coffee0127.diaop.gateway.MyLogger;

public class AuthenticationService implements IAuth {

  private final IProfileRepo profileRepo;
  private final IHash hash;
  private final IOtp otp;
  private final IFailCounter failCounter;
  private final MyLogger myLogger;

  public AuthenticationService(
      IProfileRepo profileRepo, IHash hash, IOtp otp, IFailCounter failCounter, MyLogger myLogger) {
    this.profileRepo = profileRepo;
    this.hash = hash;
    this.otp = otp;
    this.failCounter = failCounter;
    this.myLogger = myLogger;
  }

  @Override
  public boolean verify(String account, String password, String otp) {
    var isLocked = failCounter.isLocked(account);
    if (isLocked) {
      throw new FailedTooManyTimesException(account);
    }

    var passwordFromDb = profileRepo.getPassword(account);

    var hashedPassword = hash.getHashedResult(password);

    var currentOtp = this.otp.getCurrentOtp(account);

    // check valid
    if (passwordFromDb.equals(hashedPassword) && currentOtp.equals(otp)) {
      failCounter.reset(account);
      return true;
    } else {
      failCounter.add(account);

      logFailedCount(account);

      return false;
    }
  }

  private void logFailedCount(String account) {
    var failedCount = failCounter.get(account);
    myLogger.info("accountId:" + account + " failed times:" + failedCount);
  }
}
