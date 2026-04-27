package zeph_server.user.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zeph_server.user.domain.User;
import zeph_server.user.dto.UserDto;
import zeph_server.user.dto.UserUpdateDto;
import zeph_server.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

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
}
