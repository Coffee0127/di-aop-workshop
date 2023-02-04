package io.github.coffee0127.diaop.service;

import org.apache.commons.codec.digest.DigestUtils;

public class Sha256Adapter {

  public String getHashedResult(String plainText) {
    return DigestUtils.sha256Hex(plainText);
  }
}
