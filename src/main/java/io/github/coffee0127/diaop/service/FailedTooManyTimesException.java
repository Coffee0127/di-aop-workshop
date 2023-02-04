package io.github.coffee0127.diaop.service;

import lombok.Getter;

public class FailedTooManyTimesException extends RuntimeException {
  @Getter private final String account;

  public FailedTooManyTimesException(String account) {
    this.account = account;
  }
}
