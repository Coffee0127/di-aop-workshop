package com.example.external;

import java.util.Map;

public class SqlConnection implements AutoCloseable {

  private final String connectionUrl;

  public SqlConnection(String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

  public <T> T query(String namedQuery, Map<String, Object> parameters, Class<T> resultType) {
    return ReflectionUtils.newInstance(resultType);
  }

  @Override
  public void close() {}
}
