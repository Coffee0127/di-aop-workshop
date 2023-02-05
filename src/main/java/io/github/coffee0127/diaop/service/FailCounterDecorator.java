package io.github.coffee0127.diaop.service;

import io.github.coffee0127.diaop.gateway.IFailCounter;
import io.github.coffee0127.diaop.gateway.MyLogger;

public class FailCounterDecorator extends AuthDecoratorBase {

  private final IFailCounter failCounter;
  private final MyLogger myLogger;

  public FailCounterDecorator(IAuth auth, IFailCounter failCounter, MyLogger myLogger) {
    super(auth);
    this.failCounter = failCounter;
    this.myLogger = myLogger;
  }

  @Override
  public boolean verify(String account, String password, String otp) {
    checkAccountLocked(account);

    var isValid = auth.verify(account, password, otp);
    if (isValid) {
      failCounter.reset(account);
    } else {
      failCounter.add(account);
      logFailedCount(account);
    }
    return isValid;
  }

  private void checkAccountLocked(String account) {
    var isLocked = failCounter.isLocked(account);
    if (isLocked) {
      throw new FailedTooManyTimesException(account);
    }
  }

  private void logFailedCount(String account) {
    var failedCount = failCounter.get(account);
    myLogger.info("accountId:" + account + " failed times:" + failedCount);
  }
}
