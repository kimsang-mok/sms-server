package com.kimsang.smsgateway.common.util;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class CryptoUtil {
  private final Cipher cipher;

  private final RSAPrivateKey privateKey;

  private final RSAPublicKey publicKey;

  public CryptoUtil(RSAPrivateKey privateKey, RSAPublicKey publicKey) throws Exception {
    this.cipher = Cipher.getInstance("RSA");
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }

  @SneakyThrows
  public String encrypt(String value) {
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] encryptedBytes = cipher.doFinal(value.getBytes());
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  @SneakyThrows
  public String decrypt(String encryptedValue) {
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
    return new String(decryptedBytes);
  }
}
