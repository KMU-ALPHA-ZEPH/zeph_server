package zeph_server.global.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import zeph_server.global.dto.AuthResponseDto;
import zeph_server.global.jwt.TokenProvider;
import zeph_server.global.security.CustomUserDetails;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        String token = tokenProvider.createToken(user.getUser().getId());

        System.out.println("OAuth2 로그인 성공");

        AuthResponseDto dto = new AuthResponseDto(
                user.getUser().getKakaoId(),
                user.getUser().getName(),
                user.getUser().getEmail(),
                user.getUser().getProfileImageUrl(),
                token
        );

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(dto));
    }
}
