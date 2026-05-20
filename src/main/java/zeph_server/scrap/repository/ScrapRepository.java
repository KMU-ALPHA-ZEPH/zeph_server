package zeph_server.scrap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeph_server.scrap.domain.Scrap;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    long countByGroupId(Long groupId);

    boolean existsByUserIdAndCourseId(Long userId, Long id);

    List<Scrap> findAllByUserIdAndGroupId(Long userId, Long groupId);
}
