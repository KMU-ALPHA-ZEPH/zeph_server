package zeph_server.global.oauth2;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import zeph_server.global.security.CustomUserDetails;
import zeph_server.user.domain.User;
import zeph_server.user.repository.UserRepository;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 1. 카카오 id
        Long kakaoId = ((Number) attributes.get("id")).longValue();

        // 2. kakao_account
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) attributes.get("kakao_account");

        // 3. profile
        if (kakaoAccount == null) {
            throw new OAuth2AuthenticationException("Kakao account response is missing");
        }

        Map<String, Object> profile =
                (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = profile == null ? "" : (String) profile.getOrDefault("nickname", "");
        String profileImageUrl =
                profile == null ? "" : (String) profile.getOrDefault("profile_image_url", "");
        String email = (String) kakaoAccount.getOrDefault("email", "");

        // 4. kakaoId로 조회, 없으면 이메일 중복 확인 후 새 카카오 계정 생성
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> createKakaoUser(kakaoId, nickname, profileImageUrl, email));

        System.out.println("cservice");

        return new CustomUserDetails(user, attributes, Collections.emptyList());
    }

    private User createKakaoUser(
            Long kakaoId,
            String nickname,
            String profileImageUrl,
            String email
    ) {
        if (email != null && !email.isBlank()) {
            if (userRepository.existsByEmail(email)) {
                throw new OAuth2AuthenticationException("Already registered email");
            }

            return userRepository.save(User.builder()
                    .kakaoId(kakaoId)
                    .name(nickname)
                    .profileImageUrl(profileImageUrl)
                    .email(email)
                    .build());
        }

        return userRepository.save(User.builder()
                .kakaoId(kakaoId)
                .name(nickname)
                .profileImageUrl(profileImageUrl)
                .email("")
                .build());
    }
}
