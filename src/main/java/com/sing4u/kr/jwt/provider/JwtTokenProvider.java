package com.sing4u.kr.jwt.provider;

import com.sing4u.kr.common.utils.DataUtils;
import com.sing4u.kr.user.entity.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.sing4u.kr.jwt.model.JwtToken;
import com.sing4u.kr.user.enums.UserRole;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;
    private Key secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    private final String ACCOUNT_ID = "accountId";
    private final String USER_NAME = "user_name";
    private final String NICK_NAME = "nick_name";
    private static final String AUTHORITIES = "authorities";
    private static final String EMAIL = "email";

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getEncoder().encode(secret.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

//    public String generateAccessToken(String email) {
//        return Jwts.builder()
//                .setSubject(email)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
//                .signWith(secretKey, SignatureAlgorithm.HS256)
//                .compact();
//    }

    public String generateAccessToken(Long accountId,
                                      String email,
                                      List<UserRole> roles,
                                      String nickName) {
        ZonedDateTime now = ZonedDateTime.now();

        Map<String, Object> claims = getClaimsForCreation(accountId, email, roles, nickName);

        ZonedDateTime expiration = now.plusSeconds(accessTokenValidity);
        Date expirationDate = Date.from(expiration.toInstant());

        return createToken(expirationDate, claims);
    }

    private Map<String, Object> getClaimsForCreation(Long accountId,
                                                     String email,
                                                     List<UserRole> roles,
                                                     String nickName) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(ACCOUNT_ID, accountId);
        claims.put(EMAIL, email);
        claims.put(AUTHORITIES, roles);
        claims.put(USER_NAME, getUserName(roles, accountId));
        claims.put(NICK_NAME, nickName);
        return claims;
    }

    public JwtToken getAllClaimsFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();


//        List<UserRole> roles = DataUtils.cast(claims.get(AUTHORITIES, List.class), UserRole.class);
        // 1. 토큰에서 문자열 리스트를 가져옵니다.
        List<String> roleStrings = claims.get(AUTHORITIES, List.class);

//        UserType userType = UserType.valueOf(claims.get(USER_TYPE, String.class));

//        // 2. 각 문자열을 UserRole Enum으로 변환하여 새로운 리스트를 만듭니다.
        List<UserRole> roles = roleStrings.stream()
                .map(UserRole::valueOf)
                .collect(Collectors.toList());

        return JwtToken.of(
                claims.get(ACCOUNT_ID, Long.class),
                claims.getExpiration(),
                claims.get(EMAIL, String.class),
                roles,
                claims.get(NICK_NAME, String.class),
                claims.get(USER_NAME, String.class)
        );

    }

    private String createToken(Date expiration,
                               Map<String, Object> claims) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(Header.TYPE, Header.JWT_TYPE);

        return Jwts.builder()
                .setClaims(claims)
                .setHeader(headers)
                .setExpiration(expiration)
                .setIssuedAt(new Date())
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long accountId,
                                      String email,
                                      List<UserRole> roles,
                                      String nickName) {
        ZonedDateTime now = ZonedDateTime.now();

        Map<String, Object> claims = getClaimsForCreation(accountId, email, roles, nickName);

        ZonedDateTime expiration = now.plusSeconds(refreshTokenValidity);
        Date expirationDate = Date.from(expiration.toInstant());

        return createToken(expirationDate, claims);
    }

    private static String getUserName(List<UserRole> accountRoles, Long accountId) {
        UserRole role = UserRole.getMainAccountRole(accountRoles);

        return String.format("%s_%s", role, accountId);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
