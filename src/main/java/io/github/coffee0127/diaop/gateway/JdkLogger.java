package io.github.coffee0127.diaop.gateway;

import java.util.logging.Logger;

public class JdkLogger implements MyLogger {

  public JdkLogger() {}

  @Override
  public void info(String message) {
    var logger = Logger.getLogger("MyLogger");
    logger.info(message);
  }
}