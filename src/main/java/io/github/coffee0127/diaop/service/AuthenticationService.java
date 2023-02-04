package io.github.coffee0127.diaop.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationService {

  private final ProfileRepo profileRepo;
  private final Sha256Adapter sha256Adapter;
  private final OtpAdapter otpAdapter;
  private final SlackAdapter slackAdapter;

  public AuthenticationService() {
    profileRepo = new ProfileRepo();
    sha256Adapter = new Sha256Adapter();
    otpAdapter = new OtpAdapter();
    slackAdapter = new SlackAdapter();
  }

  public boolean verify(String account, String password, String otp) {
    var httpService = new HttpService();

    var isLocked = isLocked(account, httpService);
    if (isLocked) {
      throw new FailedTooManyTimesException(account);
    }
    var passwordFromDb = profileRepo.getPasswordFromDb(account);

    var hashedPassword = sha256Adapter.getHashedPassword(password);

    var currentOtp = otpAdapter.getCurrentOtp(account, httpService);

    if (passwordFromDb.equals(hashedPassword) && currentOtp.equals(otp)) {
      resetFailedCount(account, httpService);
      return true;
    } else {
      addFailedCount(account, httpService);

      logFailedCount(account, httpService);

      var message = "Account: " + account + " try to login failed";
      slackAdapter.notify(message);
      return false;
    }
  }

  private boolean isLocked(String account, HttpService httpService) {
    return Boolean.parseBoolean(
        httpService.get("https://my-api.com/api/failedCounter/isLocked?account=" + account));
  }

  private void resetFailedCount(String account, HttpService httpService) {
    httpService.post("https://my-api.com/api/failedCounter/reset?account=" + account);
  }

  private void addFailedCount(String account, HttpService httpService) {
    httpService.post("https://my-api.com/api/failedCounter/add?account=" + account);
  }

  private void logFailedCount(String account, HttpService httpService) {
    var failedCount =
        httpService.post("https://my-api.com/api/failedCounter/getFailedCount?account=" + account);
    log.info("accountId:{} failed times:{}", account, failedCount);
  }
}
