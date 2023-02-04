package io.github.coffee0127.diaop.gateway;

import org.apache.commons.codec.digest.DigestUtils;

public class Sha256Adapter {

  public Sha256Adapter() {}

  public String getHashedResult(String plainText) {
    return DigestUtils.sha256Hex(plainText);
  }
}
