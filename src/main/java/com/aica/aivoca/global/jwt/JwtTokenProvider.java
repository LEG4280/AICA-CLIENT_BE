package com.aica.aivoca.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {


    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenValidity;

    @PostConstruct
    public void init() {
        log.info("[JWT CONFIG] Access Token Expiration: {}", accessTokenValidity);
        log.info("[JWT CONFIG] Refresh Token Expiration: {}", refreshTokenValidity);
        log.info("[JWT CONFIG] Secret Key Prefix: {}", secretKey.substring(0, 5));
    }


    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    /**
     * AccessToken 생성 메서드
     */
    public String createAccessToken(Long userId, String userUid) {
        return createToken(userId, userUid, accessTokenValidity);
    }

    /**
     * RefreshToken 생성 메서드
     */
    public String createRefreshToken(Long userId, String userUid) {
        return createToken(userId, userUid, refreshTokenValidity);
    }

    private String createToken(Long userId, String userUid, long validity) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("user_uid", userUid);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        // JWT 생성
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SIGNATURE_ALGORITHM)
                .compact();
    }

    //토큰 유효성
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //토큰 받기
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
