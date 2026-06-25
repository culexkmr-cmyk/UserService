package com.culex.userService.service.auth.JWT;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenGenerator {

    private final PrivateKey privateKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    @Autowired
    public JwtTokenGenerator(PrivateKey privateKey,
                             @Value("${app.jwt.access-expiration-ms}") long accessExp,
                             @Value("${app.jwt.refresh-expiration-ms}") long refreshExp) {
        this.privateKey = privateKey;
        this.accessTokenExpirationMs = accessExp;
        this.refreshTokenExpirationMs = refreshExp;
    }

    public String generateAccessToken(String username, long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenExpirationMs)))
                .id(UUID.randomUUID().toString())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
    public RefreshTokenData generateRefreshToken(String username, long userId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(refreshTokenExpirationMs);
        String jti = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .id(jti)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
        return new RefreshTokenData(token, expiresAt, jti);
    }
}