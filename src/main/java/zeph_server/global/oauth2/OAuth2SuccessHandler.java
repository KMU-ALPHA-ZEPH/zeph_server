package zeph_server.global.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import zeph_server.global.jwt.JwtCookieProvider;
import zeph_server.global.jwt.TokenProvider;
import zeph_server.global.security.CustomUserDetails;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final JwtCookieProvider jwtCookieProvider;

    @Value("${FRONTEND_REDIRECT_URL:https://www.kmuzeph.site/splash}")
    private String frontendRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        String token = tokenProvider.createToken(user.getUser().getId());

        System.out.println("OAuth2 로그인 성공");

        response.addHeader(jwtCookieProvider.headerName(), jwtCookieProvider.createCookieHeader(token));
        response.addHeader(jwtCookieProvider.headerName(), jwtCookieProvider.createExpiredSessionCookieHeader());

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        response.sendRedirect(frontendRedirectUrl);
    }
}
