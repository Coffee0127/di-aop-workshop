package io.github.coffee0127.diaop.service;

import com.example.external.HttpClient;
import io.github.coffee0127.diaop.gateway.FailCounter;
import io.github.coffee0127.diaop.gateway.OtpAdapter;
import io.github.coffee0127.diaop.gateway.ProfileRepo;
import io.github.coffee0127.diaop.gateway.Sha256Adapter;
import io.github.coffee0127.diaop.gateway.SlackAdapter;
import java.util.logging.Logger;

public class AuthenticationService {

  private final ProfileRepo profileRepo;
  private final SlackAdapter slackAdapter;
  private final Sha256Adapter sha256Adapter;
  private final OtpAdapter otpAdapter;
  private final FailCounter failCounter;

  public AuthenticationService() {
    profileRepo = new ProfileRepo();
    slackAdapter = new SlackAdapter();
    sha256Adapter = new Sha256Adapter();
    otpAdapter = new OtpAdapter();
    failCounter = new FailCounter();
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
    var failedCountResponse =
        new HttpClient("https://joey.com")
            .post("/api/failedCounter/getFailedCount?account=" + account, Integer.class);
    failedCountResponse.ensureSuccessStatusCode();
    var logger = Logger.getLogger("MyLogger");
    logger.info("accountId:" + account + " failed times:" + failedCountResponse.read());
  }
}
