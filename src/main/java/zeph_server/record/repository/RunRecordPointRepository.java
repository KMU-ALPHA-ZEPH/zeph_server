package zeph_server.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zeph_server.record.domain.RunRecordPoint;

import java.util.List;

public interface RunRecordPointRepository extends JpaRepository<RunRecordPoint, Long> {

    List<RunRecordPoint> findByRunRecord_IdOrderBySeq(Long recordId);

    @Query("SELECT p FROM RunRecordPoint p WHERE p.runRecord.id IN :recordIds ORDER BY p.runRecord.id, p.seq")
    List<RunRecordPoint> findAllByRunRecordIdIn(@Param("recordIds") List<Long> recordIds);
}
