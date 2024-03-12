package com.hatchways.blogposts.util;

import java.time.Instant;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

public class AuthUtil {
  private final JwtEncoder jwtEncoder;

  private final String tokenIssuer;

  private final Integer tokenExpirationTime;

  public AuthUtil(String tokenIssuer, Integer tokenExpirationTime, JwtEncoder jwtEncoder) {
    this.tokenIssuer = tokenIssuer;
    this.tokenExpirationTime = tokenExpirationTime;
    this.jwtEncoder = jwtEncoder;
  }

  /** Generate a JWT token for the given user with the given expiration time. */
  public String generateToken(String username, Integer expirationInSeconds) {
    Instant now = Instant.now();

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer(tokenIssuer)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expirationInSeconds))
            .subject(username)
            .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }

  /** Generate a JWT token for the given user. */
  public String generateToken(String username) {
    return generateToken(username, tokenExpirationTime);
  }
}
