package zeph_server.scrap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeph_server.scrap.domain.Scrap;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    long countByGroupId(Long groupId);
}
