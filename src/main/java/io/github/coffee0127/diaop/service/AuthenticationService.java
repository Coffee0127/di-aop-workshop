package io.github.coffee0127.diaop.service;

import io.github.coffee0127.diaop.gateway.FailCounter;
import io.github.coffee0127.diaop.gateway.IFailCounter;
import io.github.coffee0127.diaop.gateway.IHash;
import io.github.coffee0127.diaop.gateway.IOtp;
import io.github.coffee0127.diaop.gateway.IProfileRepo;
import io.github.coffee0127.diaop.gateway.JdkLogger;
import io.github.coffee0127.diaop.gateway.MyLogger;
import io.github.coffee0127.diaop.gateway.Notification;
import io.github.coffee0127.diaop.gateway.OtpAdapter;
import io.github.coffee0127.diaop.gateway.ProfileRepo;
import io.github.coffee0127.diaop.gateway.Sha256Adapter;
import io.github.coffee0127.diaop.gateway.SlackAdapter;

public class AuthenticationService {

  private final IProfileRepo profileRepo;
  private final Notification notification;
  private final IHash hash;
  private final IOtp otp;
  private final IFailCounter failCounter;
  private final MyLogger myLogger;

  public AuthenticationService() {
    profileRepo = new ProfileRepo();
    notification = new SlackAdapter();
    hash = new Sha256Adapter();
    otp = new OtpAdapter();
    failCounter = new FailCounter();
    myLogger = new JdkLogger();
  }

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

      var message = "account:" + account + " try to login failed";
      notification.notify(message);
      return false;
    }
  }

  private void logFailedCount(String account) {
    var failedCount = failCounter.get(account);
    myLogger.info("accountId:" + account + " failed times:" + failedCount);
  }
}
