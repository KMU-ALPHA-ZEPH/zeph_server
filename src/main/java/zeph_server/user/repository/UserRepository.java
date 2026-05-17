package zeph_server.user.repository;

import zeph_server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(Long kakaoId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
