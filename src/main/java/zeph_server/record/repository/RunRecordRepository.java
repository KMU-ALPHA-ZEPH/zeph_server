package zeph_server.record.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zeph_server.record.domain.RunRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface RunRecordRepository extends JpaRepository<RunRecord, Long> {

    @EntityGraph(attributePaths = "course")
    List<RunRecord> findByUserIdOrderByStartTimeDesc(Long userId);

    @Query("""
        SELECT
            COUNT(r),
            COALESCE(SUM(r.durationSec - r.pausedSec), 0L),
            COALESCE(SUM(r.distanceKm), 0.0)
        FROM RunRecord r
        WHERE r.userId = :userId
          AND (:type IS NULL OR r.course.type = :type)
          AND (:start IS NULL OR r.startTime >= :start)
          AND (:end IS NULL OR r.startTime < :end)
    """)
    Object[] aggregateStats(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
