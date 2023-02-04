package com.example.external;

public class HttpClient {

  private final String baseUrl;

  public HttpClient() {
    this("");
  }

  public HttpClient(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public <T> Response<T> get(String url, Class<T> responseType) {
    System.out.println("Sending GET request to " + baseUrl + url);
    return new Response<>(responseType);
  }

  public <T> Response<T> post(String url, Class<T> responseType) {
    System.out.println("Sending POST request to " + baseUrl + url);
    return new Response<>(responseType);
  }
}
