package io.github.coffee0127.diaop.service;

import org.slf4j.LoggerFactory;

public class Slf4JLogAdapter {

  public void info(String message) {
    var logger = LoggerFactory.getLogger(Slf4JLogAdapter.class);
    logger.info(message);
  }
}
