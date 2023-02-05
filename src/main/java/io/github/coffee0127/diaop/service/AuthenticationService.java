package io.github.coffee0127.diaop.service;

import io.github.coffee0127.diaop.gateway.IHash;
import io.github.coffee0127.diaop.gateway.IOtp;
import io.github.coffee0127.diaop.gateway.IProfileRepo;

public class AuthenticationService implements IAuth {

  private final IProfileRepo profileRepo;
  private final IHash hash;
  private final IOtp otp;

  public AuthenticationService(IProfileRepo profileRepo, IHash hash, IOtp otp) {
    this.profileRepo = profileRepo;
    this.hash = hash;
    this.otp = otp;
  }

  @Override
  public boolean verify(String account, String password, String otp) {
    var passwordFromDb = profileRepo.getPassword(account);

    var hashedPassword = hash.getHashedResult(password);

    var currentOtp = this.otp.getCurrentOtp(account);

    // check valid
    if (passwordFromDb.equals(hashedPassword) && currentOtp.equals(otp)) {
      return true;
    } else {
      return false;
    }
  }
}
