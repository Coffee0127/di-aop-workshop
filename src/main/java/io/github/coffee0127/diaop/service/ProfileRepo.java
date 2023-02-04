package io.github.coffee0127.diaop.service;

import java.sql.DriverManager;
import java.sql.SQLException;

public class ProfileRepo {

  public String getPasswordFromDb(String account) {
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
}
