package zeph_server.courseLike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeph_server.courseLike.domain.CourseLike;

import java.util.Optional;

public interface CourseLikeRepository extends JpaRepository<CourseLike, Long> {

    // 중복 체크용
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    // 좋아요 취소 시 찾기
    Optional<CourseLike> findByUserIdAndCourseId(Long userId, Long courseId);

    // 코스별 좋아요 수 조회
    long countByCourseId(Long courseId);

    // 코스별 좋아요 일괄 삭제 (코스 삭제 시)
    void deleteByCourseId(Long courseId);
}