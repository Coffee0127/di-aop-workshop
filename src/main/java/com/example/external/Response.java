package com.example.external;

public class Response<T> {

  private final Class<T> responseType;

  public Response(Class<T> responseType) {
    this.responseType = responseType;
  }

  public boolean isSuccessStatusCode() {
    return true;
  }

  public void ensureSuccessStatusCode() {
    System.out.println("The status code should be 200!");
  }

  public T read() {
    return ReflectionUtils.newInstance(responseType);
  }
}
