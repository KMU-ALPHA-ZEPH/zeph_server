package zeph_server.record.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeph_server.course.domain.Course;
import zeph_server.course.dto.common.PathData;
import zeph_server.course.repository.CourseRepository;
import zeph_server.global.exception.CustomException;
import zeph_server.global.exception.GlobalErrorCode;
import zeph_server.record.domain.RunRecord;
import zeph_server.record.domain.RunRecordPoint;
import zeph_server.record.domain.Period;
import zeph_server.record.dto.request.RunRecordRequestDTO;
import zeph_server.record.dto.response.RunRecordCreateResponseDTO;
import zeph_server.record.dto.response.RunRecordDetailResponseDTO;
import zeph_server.record.dto.response.RunRecordListResponseDTO;
import zeph_server.record.dto.response.RunStatsResponseDTO;
import zeph_server.record.dto.response.RunStatsResponseDTO.BreakdownDto;
import zeph_server.record.repository.RunRecordPointRepository;
import zeph_server.record.repository.RunRecordRepository;
import zeph_server.courseLike.repository.CourseLikeRepository;
import zeph_server.scrap.repository.ScrapRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RunRecordService {

    private final RunRecordRepository runRecordRepository;
    private final RunRecordPointRepository pointRepository;
    private final CourseRepository courseRepository;
    private final ScrapRepository scrapRepository;
    private final CourseLikeRepository courseLikeRepository;

    @Transactional
    public RunRecordCreateResponseDTO saveRunRecord(
            Long userId,
            RunRecordRequestDTO dto
    ) {
        Course course = findCourse(dto.getCourseId());

        RunRecord savedRecord = createRunRecord(userId, dto, course);

        savePoints(
                savedRecord,
                dto.getPoints()
        );

        return new RunRecordCreateResponseDTO(savedRecord.getId());
    }

    public List<RunRecordListResponseDTO> getRecords(Long userId) {
        List<RunRecord> records =
                runRecordRepository.findByUserIdOrderByStartTimeDesc(userId);

        if(records.isEmpty()) {
            return List.of();
        }

        List<Long> recordIds = records.stream()
                .map(RunRecord::getId)
                .collect(Collectors.toList());

        Map<Long, List<RunRecordPoint>> pointsByRecord =
                pointRepository.findAllByRunRecordIdIn(recordIds).stream()
                        .collect(Collectors.groupingBy(p -> p.getRunRecord().getId()));

        return records.stream()
                .map(record -> toListResponse(
                        record,
                        pointsByRecord.getOrDefault(record.getId(), List.of())
                ))
                .collect(Collectors.toList());
    }

    public RunRecordDetailResponseDTO getRecordDetail(Long userId, Long recordId) {
        RunRecord record = findRunRecord(recordId);
        verifyOwnership(record, userId);
        Course course = record.getCourse();

        boolean isScrapped = scrapRepository.existsByUserIdAndCourseId(userId, course.getId());
        boolean isLiked = courseLikeRepository.existsByUserIdAndCourseId(userId, course.getId());

        List<RunRecordDetailResponseDTO.PointDto> coursePath =
                parseCoursePathFull(course.getPathData());

        List<RunRecordPoint> points =
                pointRepository.findByRunRecord_IdOrderBySeq(recordId);

        List<RunRecordDetailResponseDTO.PointDto> actualPath =
                points.stream()
                        .map(p -> new RunRecordDetailResponseDTO.PointDto(p.getLat(), p.getLng()))
                        .collect(Collectors.toList());

        return RunRecordDetailResponseDTO.builder()
                .runId(record.getId())
                .courseName(course.getName())
                .startTime(record.getStartTime())
                .endTime(record.getEndTime())
                .distanceKm(record.getDistanceKm())
                .durationSec(record.getDurationSec())
                .avgPace(record.getAvgPace())
                .memo(record.getMemo())
                .scrapped(isScrapped)
                .liked(isLiked)
                .coursePath(coursePath)
                .actualPath(actualPath)
                .build();
    }

    @Transactional
    public void updateMemo(Long userId, Long recordId, String memo) {
        RunRecord record = findRunRecord(recordId);
        verifyOwnership(record, userId);
        record.updateMemo(memo);
    }

    @Transactional
    public void deleteRecord(Long userId, Long recordId){

        RunRecord record =
                findRunRecord(recordId);
        verifyOwnership(record, userId);

        runRecordRepository.delete(record);
    }

    public RunStatsResponseDTO getStats(
            Long userId,
            String type,
            Period period,
            LocalDate date
    ) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (period != Period.ALL) {
            if (date == null) {
                date = LocalDate.now();
            }
            switch (period) {
                case WEEK -> {
                    LocalDate monday = date.with(DayOfWeek.MONDAY);
                    start = monday.atStartOfDay();
                    end = monday.plusWeeks(1).atStartOfDay();
                }
                case MONTH -> {
                    LocalDate first = date.withDayOfMonth(1);
                    start = first.atStartOfDay();
                    end = first.plusMonths(1).atStartOfDay();
                }
                case YEAR -> {
                    LocalDate first = date.withDayOfYear(1);
                    start = first.atStartOfDay();
                    end = first.plusYears(1).atStartOfDay();
                }
            }
        }

        String typeFilter = (type == null || "ALL".equalsIgnoreCase(type)) ? null : type;

        List<RunRecord> records = runRecordRepository.findAll(statsSpec(userId, typeFilter, start, end));

        int runCount = records.size();
        long totalDurationSec = records.stream()
                .mapToLong(RunRecord::getDurationSec)
                .sum();
        long totalMovingSec = records.stream()
                .mapToLong(r -> r.getDurationSec() - r.getPausedSec())
                .sum();
        double totalDistanceKm = records.stream()
                .mapToDouble(RunRecord::getDistanceKm)
                .sum();

        Double avgPace = null;
        if (runCount > 0 && totalDistanceKm > 0) {
            avgPace = totalMovingSec / totalDistanceKm;
        }

        List<BreakdownDto> breakdown = computeBreakdown(records, period, date);

        return RunStatsResponseDTO.builder()
                .runCount(runCount)
                .avgPace(avgPace)
                .totalDurationSec((int) totalDurationSec)
                .totalDistanceKm(totalDistanceKm)
                .breakdown(breakdown)
                .build();
    }

    private Specification<RunRecord> statsSpec(
            Long userId,
            String type,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));
            if (type != null) {
                predicates.add(cb.equal(root.get("course").get("type"), type));
            }
            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), start));
            }
            if (end != null) {
                predicates.add(cb.lessThan(root.get("startTime"), end));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Course findCourse(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow(() ->
                new CustomException(GlobalErrorCode.COURSE_NOT_FOUND)
        );
    }

    private RunRecord findRunRecord(Long recordId) {
        return runRecordRepository.findById(recordId)
                .orElseThrow(() ->
                        new CustomException(
                                GlobalErrorCode.RECORD_NOT_FOUND
                        )
                );
    }

    private RunRecord createRunRecord(Long userId, RunRecordRequestDTO dto, Course course) {
        RunRecord runRecord =
                RunRecord.builder()
                        .userId(userId)
                        .course(course)
                        .startTime(dto.getStartTime())
                        .endTime(dto.getEndTime())
                        .distanceKm(dto.getDistanceKm())
                        .durationSec(dto.getDurationSec())
                        .pausedSec(dto.getPausedSec())
                        .build();

        return runRecordRepository.save(runRecord);
    }

    private void verifyOwnership(RunRecord record, Long userId) {
        if (!record.getUserId().equals(userId)) {
            throw new CustomException(GlobalErrorCode.ACCESS_DENIED);
        }
    }

    private void savePoints(
            RunRecord savedRecord,
            List<RunRecordRequestDTO.PointDTO> points
    ){

        List<RunRecordPoint> pointList =
                new ArrayList<>();

        for(int i=0;i<points.size();i++){

            var p = points.get(i);

            pointList.add(
                    RunRecordPoint.builder()
                            .runRecord(savedRecord)
                            .seq(i)
                            .lat(p.getLat())
                            .lng(p.getLng())
                            .recordedAt(p.getRecordedAt())
                            .build()
            );
        }

        pointRepository.saveAll(pointList);
    }

    private RunRecordListResponseDTO toListResponse(RunRecord record, List<RunRecordPoint> rawActual) {
        Course course = record.getCourse();

        List<RunRecordListResponseDTO.PointDto> coursePath =
                parseCoursePath(course.getPathData());


        List<RunRecordListResponseDTO.PointDto> actualPath =
                downsample(rawActual, 50).stream()
                        .map(p -> new RunRecordListResponseDTO.PointDto(p.getLat(), p.getLng()))
                        .collect(Collectors.toList());

        return RunRecordListResponseDTO.builder()
                .runId(record.getId())
                .date(record.getStartTime())
                .courseName(course.getName())
                .distanceKm(record.getDistanceKm())
                .durationSec(record.getDurationSec())
                .avgPace(record.getAvgPace())
                .coursePath(coursePath)
                .actualPath(actualPath)
                .build();
    }

    private List<RunRecordListResponseDTO.PointDto> parseCoursePath(PathData pathData) {
        if (pathData == null || pathData.points() == null || pathData.points().isEmpty()) {
            return List.of();
        }
        return downsample(pathData.points(), 50).stream()
                .map(p -> new RunRecordListResponseDTO.PointDto(p.lat(), p.lng()))
                .collect(Collectors.toList());
    }

    private List<RunRecordDetailResponseDTO.PointDto> parseCoursePathFull(PathData pathData) {
        if (pathData == null || pathData.points() == null || pathData.points().isEmpty()) {
            return List.of();
        }
        return pathData.points().stream()
                .map(p -> new RunRecordDetailResponseDTO.PointDto(p.lat(), p.lng()))
                .collect(Collectors.toList());
    }

    private <T> List<T> downsample(List<T> points, int targetSize) {
        if (points == null || targetSize <= 1 || points.size() <= targetSize) {
            return points;
        }

        int n = points.size();
        List<T> result = new ArrayList<>(targetSize);
        for (int i = 0; i < targetSize; i++) {
            int idx = (int) Math.round((double) i * (n - 1) / (targetSize - 1));
            result.add(points.get(idx));
        }
        return result;
    }

    private List<BreakdownDto> computeBreakdown(
            List<RunRecord> records,
            Period period,
            LocalDate date
    ) {
        return switch (period) {
            case WEEK  -> computeWeekBreakdown(records);
            case MONTH -> computeMonthBreakdown(records, date);
            case YEAR  -> computeYearBreakdown(records);
            case ALL   -> computeAllBreakdown(records);
        };
    }

    private List<BreakdownDto> computeWeekBreakdown(List<RunRecord> records) {
        Map<Integer, Double> byDay = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getStartTime().getDayOfWeek().getValue(),
                        Collectors.summingDouble(RunRecord::getDistanceKm)
                ));

        String[] labels = {"월", "화", "수", "목", "금", "토", "일"};
        List<BreakdownDto> result = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            result.add(new BreakdownDto(i, labels[i - 1], byDay.getOrDefault(i, 0.0)));
        }
        return result;
    }

    private List<BreakdownDto> computeMonthBreakdown(List<RunRecord> records, LocalDate date) {
        int daysInMonth = date.lengthOfMonth();
        Map<Integer, Double> byDay = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getStartTime().getDayOfMonth(),
                        Collectors.summingDouble(RunRecord::getDistanceKm)
                ));

        List<BreakdownDto> result = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            result.add(new BreakdownDto(i, String.valueOf(i), byDay.getOrDefault(i, 0.0)));
        }
        return result;
    }

    private List<BreakdownDto> computeYearBreakdown(List<RunRecord> records) {
        Map<Integer, Double> byMonth = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getStartTime().getMonthValue(),
                        Collectors.summingDouble(RunRecord::getDistanceKm)
                ));

        List<BreakdownDto> result = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            result.add(new BreakdownDto(i, i + "월", byMonth.getOrDefault(i, 0.0)));
        }
        return result;
    }

    private List<BreakdownDto> computeAllBreakdown(List<RunRecord> records) {
        if (records.isEmpty()) {
            return List.of();
        }

        Map<Integer, Double> byYear = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getStartTime().getYear(),
                        Collectors.summingDouble(RunRecord::getDistanceKm)
                ));

        return byYear.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new BreakdownDto(e.getKey(), String.valueOf(e.getKey()), e.getValue()))
                .collect(Collectors.toList());
    }
}
