package zeph_server.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zeph_server.course.domain.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    List<Course> findByType(String type);

    List<Course> findByRegion(String region);

    List<Course> findByRegionAndType(String region, String type);

    Boolean existsByUserIdAndName(Long userId, String name);

    // 이름 중복 검사 시 수정 대상 코스 자신은 제외
    boolean existsByUserIdAndNameAndIdNot(Long userId, String name, Long id);

    Optional<Course> findByIdAndUserId(Long courseId, Long userId);
}
