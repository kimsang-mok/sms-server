package com.kimsang.smsgateway.config;

import com.kimsang.smsgateway.common.util.CryptoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class CryptoConfig {
  @Value("${jwt.private-key}")
  private Resource privateKeyResource;

  @Value("${jwt.public-key}")
  private Resource publicKeyResource;

  @Bean
  public CryptoUtil cryptoUtil() throws Exception {
    String privateKeyContent = new String(privateKeyResource.getInputStream().readAllBytes())
        .replaceAll("-----\\w+ PRIVATE KEY-----", "")
        .replaceAll("\\s", "");
    String publicKeyContent = new String(publicKeyResource.getInputStream().readAllBytes())
        .replaceAll("-----\\w+ PUBLIC KEY-----", "")
        .replaceAll("\\s", "");

    KeyFactory kf = KeyFactory.getInstance("RSA");

    PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
    RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(privateKeySpec);

    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
    RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(publicKeySpec);

    return new CryptoUtil(privateKey, publicKey);
  }
}
