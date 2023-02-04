package io.github.coffee0127.diaop.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
public class AuthenticationService {

  private final ProfileRepo profileRepo;

  public AuthenticationService() {
    profileRepo = new ProfileRepo();
  }

  public boolean verify(String account, String password, String otp) {
    var httpService = new HttpService();

    var isLocked = isLocked(account, httpService);
    if (isLocked) {
      throw new FailedTooManyTimesException(account);
    }
    var passwordFromDb = profileRepo.getPasswordFromDb(account);

    var hashedPassword = getHashedPassword(password);

    var currentOtp = getCurrentOtp(account, httpService);

    if (passwordFromDb.equals(hashedPassword) && currentOtp.equals(otp)) {
      resetFailedCount(account, httpService);
      return true;
    } else {
      addFailedCount(account, httpService);

      logFailedCount(account, httpService);

      var message = "Account: " + account + " try to login failed";
      notify(message);
      return false;
    }
  }

  private boolean isLocked(String account, HttpService httpService) {
    return Boolean.parseBoolean(
        httpService.get("https://my-api.com/api/failedCounter/isLocked?account=" + account));
  }

  private String getHashedPassword(String password) {
    return DigestUtils.sha256Hex(password);
  }

  private String getCurrentOtp(String account, HttpService httpService) {
    return httpService.get("https://my-api.com/otp?account=" + account);
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

  private void notify(String message) {
    new SlackClient().postMessage(message);
  }
}
