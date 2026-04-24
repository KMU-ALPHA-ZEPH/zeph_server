package zeph_server.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeph_server.record.domain.RunRecordPoint;

import java.util.List;

public interface RunRecordPointRepository extends JpaRepository<RunRecordPoint, Long> {

    List<RunRecordPoint> findByRunRecordIdOrderBySeq(Long runRecordId);
}
