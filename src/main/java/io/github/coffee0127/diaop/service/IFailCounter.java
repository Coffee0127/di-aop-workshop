package io.github.coffee0127.diaop.service;

public interface IFailCounter {

  boolean isLocked(String account);

  void reset(String account);

  void add(String account);

  String get(String account);
}
