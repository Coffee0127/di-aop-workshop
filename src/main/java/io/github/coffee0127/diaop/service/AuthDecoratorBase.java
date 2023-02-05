package io.github.coffee0127.diaop.service;

public abstract class AuthDecoratorBase implements IAuth {

  protected final IAuth auth;

  protected AuthDecoratorBase(IAuth auth) {
    this.auth = auth;
  }
}
