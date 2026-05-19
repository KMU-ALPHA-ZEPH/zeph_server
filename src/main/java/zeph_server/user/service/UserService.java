package zeph_server.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zeph_server.global.dto.AuthResponseDto;
import zeph_server.global.jwt.TokenProvider;
import zeph_server.user.domain.User;
import zeph_server.user.dto.EmailLoginRequest;
import zeph_server.user.dto.EmailSignupRequest;
import zeph_server.user.dto.UserDto;
import zeph_server.user.dto.UserUpdateDto;
import zeph_server.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public void signup(EmailSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .build();

        userRepository.save(user);
    }

    public AuthResponseDto login(EmailLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "이메일 또는 비밀번호가 올바르지 않습니다."
                ));

        if (user.getPassword() == null
                || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        String token = tokenProvider.createToken(user.getId());

        return new AuthResponseDto(
                user.getKakaoId(),
                user.getName(),
                user.getEmail(),
                user.getProfileImageUrl(),
                token
        );
    }

    public UserDto getProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        return new UserDto(
                user.getId(),
                user.getKakaoId(),
                user.getEmail(),
                user.getName(),
                user.getProfileImageUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
    public void updateProfile(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        user.setName(dto.name());
        user.setProfileImageUrl((dto.profile_image_url()));
    }

    @Transactional
    public void deleteProfile(Long id){
        userRepository.deleteById(id);
    }
}
