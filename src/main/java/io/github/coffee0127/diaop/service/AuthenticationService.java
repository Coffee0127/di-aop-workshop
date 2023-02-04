package io.github.coffee0127.diaop.service;

import io.github.coffee0127.diaop.gateway.FailCounter;
import io.github.coffee0127.diaop.gateway.JdkLogger;
import io.github.coffee0127.diaop.gateway.OtpAdapter;
import io.github.coffee0127.diaop.gateway.ProfileRepo;
import io.github.coffee0127.diaop.gateway.Sha256Adapter;
import io.github.coffee0127.diaop.gateway.SlackAdapter;

public class AuthenticationService {

  private final ProfileRepo profileRepo;
  private final SlackAdapter slackAdapter;
  private final Sha256Adapter sha256Adapter;
  private final OtpAdapter otpAdapter;
  private final FailCounter failCounter;
  private final JdkLogger jdkLogger;

  public AuthenticationService() {
    profileRepo = new ProfileRepo();
    slackAdapter = new SlackAdapter();
    sha256Adapter = new Sha256Adapter();
    otpAdapter = new OtpAdapter();
    failCounter = new FailCounter();
    jdkLogger = new JdkLogger();
  }

  public boolean verify(String account, String password, String otp) {
    var isLocked = failCounter.isLocked(account);
    if (isLocked) {
      throw new FailedTooManyTimesException(account);
    }

    var passwordFromDb = profileRepo.getPassword(account);

    var hashedPassword = sha256Adapter.getHashedResult(password);

    var currentOtp = otpAdapter.getCurrentOtp(account);

    // check valid
    if (passwordFromDb.equals(hashedPassword) && currentOtp.equals(otp)) {
      failCounter.reset(account);
      return true;
    } else {
      failCounter.add(account);

      logFailedCount(account);

      var message = "account:" + account + " try to login failed";
      slackAdapter.notify(message);
      return false;
    }
  }

  private void logFailedCount(String account) {
    var failedCount = failCounter.get(account);
    jdkLogger.info("accountId:" + account + " failed times:" + failedCount);
  }
}
