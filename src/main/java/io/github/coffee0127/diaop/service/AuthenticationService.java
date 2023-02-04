package io.github.coffee0127.diaop.service;

import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.codec.digest.DigestUtils;

public class AuthenticationService {

  public boolean verify(String account, String password, String otp) {
    var httpService = new HttpService();

    // check account is locked
    var isLocked =
        httpService.get("https://my-api.com/api/failedCounter/isLocked?account=" + account);
    if ("true".equals(isLocked)) {
      throw new FailedTooManyTimesException(account);
    }
    // Step 1: find DB password by account
    String passwordFromDb;
    try (var connection = DriverManager.getConnection("jdbc:h2:mem:my_app", "sa", "")) {
      var pstmt = connection.prepareStatement("SELECT * FROM USER WHERE account = ?");
      pstmt.setString(1, account);
      var resultSet = pstmt.executeQuery();
      if (resultSet.next()) {
        passwordFromDb = resultSet.getString("password");
      } else {
        throw new RuntimeException("Cannot find the password");
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    // Step 2: hash the parameter password
    var hashedPassword = DigestUtils.sha256Hex(password);

    // Step 3: get the current OTP by account
    var currentOtp = httpService.get("https://my-api.com/otp?account=" + account);

    // Step 4: verification
    if (passwordFromDb.equals(hashedPassword) && currentOtp.equals(otp)) {
      return true;
    } else {
      // notify user
      var message = "Account: " + account + " try to login failed";
      new SlackClient().postMessage(message);
      return false;
    }
  }
}
