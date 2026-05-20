package zeph_server.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zeph_server.course.domain.Course;
import zeph_server.group.domain.Group;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findAllByUserId(Long userId);

    Boolean existsByUserIdAndName(Long userId, String name);

}
