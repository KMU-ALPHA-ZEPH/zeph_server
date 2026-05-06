package zeph_server.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zeph_server.group.domain.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {

}
