package io.github.coffee0127.diaop.service;

import com.example.external.HttpClient;
import io.github.coffee0127.diaop.gateway.ProfileRepo;
import io.github.coffee0127.diaop.gateway.SlackAdapter;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;

public class AuthenticationService {

  private final ProfileRepo profileRepo;
  private final SlackAdapter slackAdapter;

  public AuthenticationService() {
    profileRepo = new ProfileRepo();
    slackAdapter = new SlackAdapter();
  }

  public boolean verify(String account, String password, String otp) {
    var httpClient = new HttpClient("https://joey.com");

    var isLocked = isLocked(account, httpClient);
    if (isLocked) {
      throw new FailedTooManyTimesException(account);
    }

    var passwordFromDb = profileRepo.getPasswordFromDb(account);

    var hashedPassword = getHashedPassword(password);

    var currentOtp = getCurrentOtp(account, httpClient);

    // check valid
    if (passwordFromDb.equals(hashedPassword) && currentOtp.equals(otp)) {
      resetFailedCount(account, httpClient);
      return true;
    } else {
      addFailedCount(account, httpClient);

      logFailedCount(account, httpClient);

      var message = "account:" + account + " try to login failed";
      slackAdapter.notify(message);
      return false;
    }
  }

  private boolean isLocked(String account, HttpClient httpClient) {
    var isLockedResponse =
        httpClient.get("/api/failedCounter/isLocked?account=" + account, Boolean.class);
    isLockedResponse.ensureSuccessStatusCode();
    return isLockedResponse.read();
  }

  private String getHashedPassword(String password) {
    return DigestUtils.sha256Hex(password);
  }

  private String getCurrentOtp(String account, HttpClient httpClient) {
    var response = httpClient.get("/api/otps?account" + account, String.class);
    if (response.isSuccessStatusCode()) {
    } else {
      throw new RuntimeException("web api error, accountId:" + account);
    }
    return response.read();
  }

  private void resetFailedCount(String account, HttpClient httpClient) {
    var resetResponse = httpClient.post("/api/failedCounter/reset?account=" + account, Void.class);
    resetResponse.ensureSuccessStatusCode();
  }

  private void addFailedCount(String account, HttpClient httpClient) {
    var addFailedCountResponse =
        httpClient.post("/api/failedCounter/add?account=" + account, Void.class);
    addFailedCountResponse.ensureSuccessStatusCode();
  }

  private void logFailedCount(String account, HttpClient httpClient) {
    var failedCountResponse =
        httpClient.post("/api/failedCounter/getFailedCount?account=" + account, Integer.class);
    failedCountResponse.ensureSuccessStatusCode();
    var logger = Logger.getLogger("MyLogger");
    logger.info("accountId:" + account + " failed times:" + failedCountResponse.read());
  }
}
