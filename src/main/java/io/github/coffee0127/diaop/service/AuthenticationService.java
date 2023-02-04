package io.github.coffee0127.diaop.service;

import com.example.external.HttpClient;
import com.example.external.SlackClient;
import com.example.external.SqlConnection;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;

public class AuthenticationService {

  public boolean verify(String account, String password, String otp) {
    var httpClient = new HttpClient("https://joey.com");

    var isLockedResponse =
        httpClient.get("/api/failedCounter/isLocked?account=" + account, Boolean.class);
    isLockedResponse.ensureSuccessStatusCode();
    if (isLockedResponse.read()) {
      throw new FailedTooManyTimesException(account);
    }

    // get password from DB
    String passwordFromDb;
    try (var connection = new SqlConnection("jdbc:h2:mem:my_app")) {
      passwordFromDb = connection.query("GET_PASSWORD_SQL", Map.of("ID", account), String.class);
    }

    // hash input password
    var hashedPassword = DigestUtils.sha256Hex(password);

    // get current otp
    var response = httpClient.get("/api/otps?account" + account, String.class);
    if (response.isSuccessStatusCode()) {
    } else {
      throw new RuntimeException("web api error, accountId:" + account);
    }
    var currentOtp = response.read();

    // check valid
    if (passwordFromDb.equals(hashedPassword) && currentOtp.equals(otp)) {
      return true;
    } else {
      var message = "account:" + account + " try to login failed";
      var slackClient = new SlackClient("<YOUR_API_TOKEN>");
      slackClient.postMessage("#my-channel", message);

      return false;
    }
  }
}
