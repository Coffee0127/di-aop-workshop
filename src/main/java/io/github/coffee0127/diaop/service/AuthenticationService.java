package io.github.coffee0127.diaop.service;

import com.example.external.HttpClient;
import com.example.external.SlackClient;
import com.example.external.SqlConnection;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;

public class AuthenticationService {

  public boolean verify(String account, String password, String otp) {
    var httpClient = new HttpClient("https://joey.com");

    var isLocked = isLocked(account, httpClient);
    if (isLocked) {
      throw new FailedTooManyTimesException(account);
    }

    var passwordFromDb = getPasswordFromDb(account);

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
      notify(message);
      return false;
    }
  }

  private boolean isLocked(String account, HttpClient httpClient) {
    var isLockedResponse =
        httpClient.get("/api/failedCounter/isLocked?account=" + account, Boolean.class);
    isLockedResponse.ensureSuccessStatusCode();
    return isLockedResponse.read();
  }

  private String getPasswordFromDb(String account) {
    String passwordFromDb;
    try (var connection = new SqlConnection("jdbc:h2:mem:my_app")) {
      passwordFromDb = connection.query("GET_PASSWORD_SQL", Map.of("ID", account), String.class);
    }
    return passwordFromDb;
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

  private void notify(String message) {
    var slackClient = new SlackClient("<YOUR_API_TOKEN>");
    slackClient.postMessage("#my-channel", message);
  }
}
