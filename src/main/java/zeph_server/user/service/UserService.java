package zeph_server.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import zeph_server.global.dto.AuthResponseDto;
import zeph_server.global.jwt.TokenProvider;
import zeph_server.global.s3.S3ImageService;
import zeph_server.user.domain.User;
import zeph_server.user.dto.EmailLoginRequest;
import zeph_server.user.dto.EmailSignupRequest;
import zeph_server.user.dto.PasswordResetConfirmRequest;
import zeph_server.user.dto.PasswordResetRequest;
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
    private final PasswordResetMailService passwordResetMailService;
    private final S3ImageService s3ImageService;

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
                profileImageUrl(user),
                token
        );
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        userRepository.findByEmail(request.email())
                .filter(user -> user.getPassword() != null)
                .ifPresent(user -> {
                    String token = tokenProvider.createPasswordResetToken(
                            user.getId(),
                            user.getPassword()
                    );
                    passwordResetMailService.sendPasswordResetMail(user.getEmail(), token);
                });
    }

    @Transactional
    public void resetPassword(PasswordResetConfirmRequest request) {
        try {
            Long userId = tokenProvider.getPasswordResetUserId(request.token());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "유효하지 않은 비밀번호 재설정 토큰입니다."
                    ));

            if (user.getPassword() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "이메일 로그인 계정이 아닙니다."
                );
            }

            tokenProvider.validatePasswordResetPasswordHash(request.token(), user.getPassword());
            user.setPassword(passwordEncoder.encode(request.newPassword()));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "유효하지 않은 비밀번호 재설정 토큰입니다."
            );
        }
    }

    public UserDto getProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        return new UserDto(
                user.getId(),
                user.getKakaoId(),
                user.getEmail(),
                user.getName(),
                profileImageUrl(user),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
    public void updateProfile(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        user.setName(dto.name());
    }

    @Transactional
    public void updateProfile(Long id, String name, MultipartFile image) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        user.setName(name);
        if (image != null && !image.isEmpty()) {
            user.setProfileImageKey(s3ImageService.uploadProfileImage(id, image));
        }
    }

    @Transactional
    public void deleteProfile(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found."));
    }

    private String profileImageUrl(User user) {
        if (user.getProfileImageKey() != null && !user.getProfileImageKey().isBlank()) {
            return s3ImageService.toPublicUrl(user.getProfileImageKey());
        }
        return user.getProfileImageUrl();
    }
}
