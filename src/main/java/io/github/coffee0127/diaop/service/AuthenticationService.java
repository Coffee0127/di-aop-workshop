package io.github.coffee0127.diaop.service;

import com.example.external.HttpClient;
import com.example.external.SqlConnection;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;

public class AuthenticationService {

  public boolean verify(String account, String password, String otp) {
    // get password from DB
    String passwordFromDb;
    try (var connection = new SqlConnection("jdbc:h2:mem:my_app")) {
      passwordFromDb = connection.query("GET_PASSWORD_SQL", Map.of("ID", account), String.class);
    }

    // hash input password
    var hashedPassword = DigestUtils.sha256Hex(password);

    // get current otp
    var response =
        new HttpClient("https://joey.com").get("/api/otps?account" + account, String.class);
    if (response.isSuccessStatusCode()) {
    } else {
      throw new RuntimeException("web api error, accountId:" + account);
    }
    var currentOtp = response.read();

    // check valid
    if (passwordFromDb.equals(hashedPassword) && currentOtp.equals(otp)) {
      return true;
    } else {
      return false;
    }
  }
}
