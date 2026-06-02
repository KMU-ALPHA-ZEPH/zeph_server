package zeph_server.scrap.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import zeph_server.scrap.domain.Scrap;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    long countByGroupId(Long groupId);

    boolean existsByUserIdAndCourseId(Long userId, Long id);

    List<Scrap> findAllByUserId(Long userId);

    List<Scrap> findAllByUserIdAndGroupId(Long userId, Long groupId);

    // 코스 상세에서 현재 유저의 스크랩 단건 조회
    Optional<Scrap> findByUserIdAndCourseId(Long userId, Long courseId);

    // 코스 목록에서 현재 유저의 스크랩 일괄 조회 (N+1 방지)
    List<Scrap> findAllByUserIdAndCourseIdIn(Long userId, Collection<Long> courseIds);

    List<Scrap> findAllByGroupId(Long groupId);

    @Modifying
    @Query("update Scrap s set s.group = null where s.group.id = :groupId")
    void clearGroupByGroupId(@Param("groupId") Long groupId);
}
