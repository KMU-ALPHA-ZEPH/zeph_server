package zeph_server.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zeph_server.course.domain.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    List<Course> findByType(String type);

    List<Course> findByRegion(String region);

    List<Course> findByRegionAndType(String region, String type);
}
