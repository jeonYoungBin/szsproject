package com.szs.szsproject.utils;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    @Value("${jwt.secretKey}")
    private String secretKey;

    // 토큰 유효시간 30분
    private static long tokenValidTime = 2;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    //JWT 토큰 생성
    public String createToken(String userId, String regNo) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("regNo", regNo);
        Date now = new Date();
        Duration tokenValidity = Duration.ofHours(tokenValidTime); // 2시간 유효

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(Date.from(now.toInstant().plus(tokenValidity))) // 명확한 만료 시간 설정
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    //토큰의 유효성 + 만료일자 확인
    public boolean validationToken(String jstToken) throws JwtException {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jstToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
    }

    public String getMemberInfo(String jwt) throws RuntimeException {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt)
                    .getBody().get("userId", String.class);

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

}
