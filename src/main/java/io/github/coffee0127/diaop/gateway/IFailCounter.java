package io.github.coffee0127.diaop.gateway;

public interface IFailCounter {

  boolean isLocked(String account);

  void reset(String account);

  void add(String account);

  Integer get(String account);
}
