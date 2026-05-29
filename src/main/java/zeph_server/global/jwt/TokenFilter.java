package zeph_server.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import lombok.RequiredArgsConstructor;
import zeph_server.global.security.CustomUserDetails;
import zeph_server.user.domain.User;

@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final JwtCookieProvider jwtCookieProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        if (token != null) {

            if (tokenProvider.validate(token)) {
                Long userId = tokenProvider.getUserId(token);

                CustomUserDetails userDetails =
                        new CustomUserDetails(
                                User.builder()
                                        .id(userId)
                                        .build(),
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return jwtCookieProvider.resolveToken(request);
    }
}
