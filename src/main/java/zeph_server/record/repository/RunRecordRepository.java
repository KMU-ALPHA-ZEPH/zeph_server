package zeph_server.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zeph_server.record.domain.RunRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface RunRecordRepository extends JpaRepository<RunRecord, Long> {
    List<RunRecord> findByUserIdOrderByStartTimeDesc(Long userId);

    @Query("SELECT SUM(r.distanceKm) FROM RunRecord r WHERE r.userId = :userId")
    Double getTotalDistance(@Param("userId") Long userId);

    @Query("SELECT SUM(r.distanceKm) FROM RunRecord r " + "WHERE r.userId = :userId AND r.startTime >= :start")
    Double getMonthlyDistance(@Param("userId") Long userId, @Param("start") LocalDateTime start);
}
