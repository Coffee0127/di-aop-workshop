package io.github.coffee0127.diaop.gateway;

import com.example.external.SqlConnection;
import java.util.Map;

public class ProfileRepo {

  public ProfileRepo() {}

  public String getPasswordFromDb(String account) {
    String passwordFromDb;
    try (var connection = new SqlConnection("jdbc:h2:mem:my_app")) {
      passwordFromDb = connection.query("GET_PASSWORD_SQL", Map.of("ID", account), String.class);
    }
    return passwordFromDb;
  }
}
