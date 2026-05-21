package com.ferry.framework.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;

public class JwtTokenService {
    private final SecretKey key;

    public JwtTokenService(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String subject, Duration ttl) {
        Date now = new Date();
        return Jwts.builder().subject(subject).issuedAt(now).expiration(new Date(now.getTime() + ttl.toMillis())).signWith(key).compact();
    }

    public String parseSubject(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
