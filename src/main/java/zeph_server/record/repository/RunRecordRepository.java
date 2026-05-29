package zeph_server.record.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zeph_server.record.domain.RunRecord;

import java.util.List;

public interface RunRecordRepository
        extends JpaRepository<RunRecord, Long>, JpaSpecificationExecutor<RunRecord> {

    @EntityGraph(attributePaths = "course")
    List<RunRecord> findByUserIdOrderByStartTimeDesc(Long userId);
}
