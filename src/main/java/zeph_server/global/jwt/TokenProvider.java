package zeph_server.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider {
    private static final String PASSWORD_RESET_PURPOSE = "password_reset";

    private final Key key;
    private final long expiration;
    private final long passwordResetExpiration;

    public TokenProvider(
            @Value("${JWT_SECRET}") String secret,
            @Value("${JWT_EXPIRATION:3600000}") long expiration,
            @Value("${PASSWORD_RESET_TOKEN_EXPIRATION:900000}") long passwordResetExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
        this.passwordResetExpiration = passwordResetExpiration;
    }

    public String createToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String createPasswordResetToken(Long userId, String passwordHash) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("purpose", PASSWORD_RESET_PURPOSE)
                .claim("passwordHash", passwordHash)
                .setExpiration(new Date(System.currentTimeMillis() + passwordResetExpiration))
                .signWith(key)
                .compact();
    }

    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (claims.get("purpose", String.class) != null) {
            throw new IllegalArgumentException("Invalid access token.");
        }

        return Long.parseLong(claims.getSubject());
    }

    public Long getPasswordResetUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (!PASSWORD_RESET_PURPOSE.equals(claims.get("purpose", String.class))) {
            throw new IllegalArgumentException("Invalid password reset token.");
        }

        return Long.parseLong(claims.getSubject());
    }

    public void validatePasswordResetPasswordHash(String token, String currentPasswordHash) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (!currentPasswordHash.equals(claims.get("passwordHash", String.class))) {
            throw new IllegalArgumentException("Expired password reset token.");
        }
    }

    public boolean validate(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("purpose", String.class) == null;
        } catch (Exception e) {
            return false;
        }
    }
}
