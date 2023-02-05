package io.github.coffee0127.diaop.service;

import io.github.coffee0127.diaop.gateway.IFailCounter;

public class FailCounterDecorator implements IAuth {

  private final IAuth auth;
  private final IFailCounter failCounter;

  public FailCounterDecorator(IAuth auth, IFailCounter failCounter) {
    this.auth = auth;
    this.failCounter = failCounter;
  }

  @Override
  public boolean verify(String account, String password, String otp) {
    checkAccountLocked(account);

    var isValid = auth.verify(account, password, otp);
    if (isValid) {
      failCounter.reset(account);
    } else {
      failCounter.add(account);
    }
    return isValid;
  }

  private void checkAccountLocked(String account) {
    var isLocked = failCounter.isLocked(account);
    if (isLocked) {
      throw new FailedTooManyTimesException(account);
    }
  }
}
