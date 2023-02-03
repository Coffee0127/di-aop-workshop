package io.github.coffee0127.diaop.digest;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DigestTest {

  @Test
  void md5_should_work() {
    assertEquals("b10a8db164e0754105b7a99be72e3fe5", DigestUtils.md5Hex("Hello World"));
  }

  @Test
  void sha512_should_work() {
    assertEquals(
        "2c74fd17edafd80e8447b0d46741ee243b7eb74dd2149a0ab1b9246fb30382f27e853d8585719e0e67cbda0daa8f51671064615d645ae27acb15bfb1447f459b",
        DigestUtils.sha512Hex("Hello World"));
  }
}
