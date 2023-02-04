package io.github.coffee0127.diaop.service;

import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
public class AuthenticationService {

  public boolean verify(String account, String password, String otp) {
    var httpService = new HttpService();

    var isLocked = isLocked(account, httpService);
    if (isLocked) {
      throw new FailedTooManyTimesException(account);
    }
    var passwordFromDb = getPasswordFromDb(account);

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

  private String getPasswordFromDb(String account) {
    try (var connection = DriverManager.getConnection("jdbc:h2:mem:my_app", "sa", "")) {
      var pstmt = connection.prepareStatement("SELECT * FROM USER WHERE account = ?");
      pstmt.setString(1, account);
      var resultSet = pstmt.executeQuery();
      if (resultSet.next()) {
        return resultSet.getString("password");
      }
      throw new RuntimeException("Cannot find the password");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
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
