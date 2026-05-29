package zeph_server.global.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class JwtCookieProvider {
    private final String cookieName;
    private final long expiration;

    public JwtCookieProvider(
            @Value("${JWT_COOKIE_NAME:accessToken}") String cookieName,
            @Value("${JWT_EXPIRATION:3600000}") long expiration
    ) {
        this.cookieName = cookieName;
        this.expiration = expiration;
    }

    public String createCookieHeader(String token) {
        return ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofMillis(expiration))
                .build()
                .toString();
    }

    public String createExpiredCookieHeader() {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build()
                .toString();
    }

    public String createExpiredSessionCookieHeader() {
        return ResponseCookie.from("JSESSIONID", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build()
                .toString();
    }

    public String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public String headerName() {
        return HttpHeaders.SET_COOKIE;
    }
}
