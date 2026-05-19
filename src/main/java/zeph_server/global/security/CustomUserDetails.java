package zeph_server.global.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import zeph_server.user.domain.User;

import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
@Getter
public class CustomUserDetails implements OAuth2User {

    private User user;
    private Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(user.getKakaoId());
    }
}
