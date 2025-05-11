package com.sing4u.kr.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.sing4u.kr.common.utils.DataUtils;
import com.sing4u.kr.jwt.model.JwtToken;
import com.sing4u.kr.user.entity.enums.UserType;
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
    private final String USER_TYPE = "user_type";
    private final String USER_NAME = "user_name";
    private final String NICK_NAME = "nick_name";
    private static final String AUTHORITIES = "authorities";

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getEncoder().encode(secret.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(Long accountId,
                                      List<UserRole> roles,
                                      String nickName,
                                      UserType userType) {
        ZonedDateTime now = ZonedDateTime.now();

        Map<String, Object> claims = getClaimsForCreation(accountId, roles, nickName, userType);

        ZonedDateTime expiration = now.plusSeconds(accessTokenValidity);
        Date expirationDate = Date.from(expiration.toInstant());

        return createToken(expirationDate, claims);
    }

    private Map<String, Object> getClaimsForCreation(Long accountId,
                                                     List<UserRole> roles,
                                                     String nickName,
                                                     UserType userType) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(ACCOUNT_ID, accountId);
        claims.put(AUTHORITIES, roles);
        claims.put(USER_NAME, getUserName(roles, accountId));
        claims.put(NICK_NAME, nickName);
        claims.put(USER_TYPE, userType);
        return claims;
    }

    public JwtToken getAllClaimsFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<UserRole> roles = DataUtils.cast(claims.get(AUTHORITIES, List.class), UserRole.class);
        UserType userType = UserType.valueOf(claims.get(USER_TYPE, String.class));

        return JwtToken.of(
                claims.get(ACCOUNT_ID, Long.class),
                claims.getExpiration(),
                roles,
                claims.get(NICK_NAME, String.class),
                claims.get(USER_NAME, String.class),
                userType
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
                                      List<UserRole> roles,
                                      String nickName,
                                      UserType userType) {
        ZonedDateTime now = ZonedDateTime.now();

        Map<String, Object> claims = getClaimsForCreation(accountId, roles, nickName, userType);

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
