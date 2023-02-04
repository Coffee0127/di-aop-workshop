package io.github.coffee0127.diaop.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationService {

  private final ProfileRepo profileRepo;
  private final Sha256Adapter sha256Adapter;
  private final OtpAdapter otpAdapter;
  private final SlackAdapter slackAdapter;
  private final FailCounter failCounter;

  public AuthenticationService() {
    profileRepo = new ProfileRepo();
    sha256Adapter = new Sha256Adapter();
    otpAdapter = new OtpAdapter();
    slackAdapter = new SlackAdapter();
    failCounter = new FailCounter();
  }

  public boolean verify(String account, String password, String otp) {
    var httpService = new HttpService();

    var isLocked = failCounter.isLocked(account);
    if (isLocked) {
      throw new FailedTooManyTimesException(account);
    }
    var passwordFromDb = profileRepo.getPassword(account);

    var hashedPassword = sha256Adapter.getHashedResult(password);

    var currentOtp = otpAdapter.getCurrentOtp(account, httpService);

    if (passwordFromDb.equals(hashedPassword) && currentOtp.equals(otp)) {
      failCounter.reset(account);
      return true;
    } else {
      failCounter.add(account);

      logFailedCount(account, httpService);

      var message = "Account: " + account + " try to login failed";
      slackAdapter.notify(message);
      return false;
    }
  }

  private void logFailedCount(String account, HttpService httpService) {
    var failedCount =
        httpService.post("https://my-api.com/api/failedCounter/getFailedCount?account=" + account);
    log.info("accountId:{} failed times:{}", account, failedCount);
  }
}
