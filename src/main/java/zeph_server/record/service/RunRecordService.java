package zeph_server.record.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeph_server.course.domain.Course;
import zeph_server.course.dto.common.PathData;
import zeph_server.course.dto.common.Point;
import zeph_server.course.repository.CourseRepository;
import zeph_server.global.exception.CustomException;
import zeph_server.global.exception.GlobalErrorCode;
import zeph_server.record.domain.RunRecord;
import zeph_server.record.domain.RunRecordPoint;
import zeph_server.record.dto.request.RunRecordRequestDTO;
import zeph_server.record.dto.response.RunRecordCreateResponseDTO;
import zeph_server.record.dto.response.RunRecordDetailResponseDTO;
import zeph_server.record.dto.response.RunRecordListResponseDTO;
import zeph_server.record.dto.response.RunStatsResponseDTO;
import zeph_server.record.repository.RunRecordPointRepository;
import zeph_server.record.repository.RunRecordRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RunRecordService {

    private final RunRecordRepository runRecordRepository;
    private final RunRecordPointRepository pointRepository;
    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public RunRecordCreateResponseDTO saveRunRecord(
            RunRecordRequestDTO dto
    ) {
        Course course = findCourse(dto.getCourseId());

        RunRecord savedRecord = createRunRecord(dto, course);

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

    public RunRecordDetailResponseDTO getRecordDetail(Long recordId) {
        RunRecord record = findRunRecord(recordId);
        Course course = record.getCourse();

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
                .courseName(course.getType())
                .startTime(record.getStartTime())
                .endTime(record.getEndTime())
                .distanceKm(record.getDistanceKm())
                .durationSec(record.getDurationSec())
                .avgPace(record.getAvgPace())
                .memo(record.getMemo())
                .coursePath(coursePath)
                .actualPath(actualPath)
                .build();
    }

    @Transactional
    public void updateMemo(Long recordId, String memo) {
        RunRecord record = findRunRecord(recordId);
        record.updateMemo(memo);
    }

    @Transactional
    public void deleteRecord(Long recordId){

        RunRecord record =
                findRunRecord(recordId);

        runRecordRepository.delete(record);
    }

    public RunStatsResponseDTO getStats(
            Long userId
    ){

        Double totalDistance =
                runRecordRepository
                        .getTotalDistance(userId);

        Double monthlyDistance =
                runRecordRepository
                        .getMonthlyDistance(
                                userId,
                                LocalDateTime.now()
                                        .withDayOfMonth(1)
                                        .withHour(0)
                                        .withMinute(0)
                                        .withSecond(0)
                        );

        return RunStatsResponseDTO.builder()
                .totalDistance(
                        totalDistance == null
                                ? 0.0
                                : totalDistance
                )
                .monthlyDistance(
                        monthlyDistance == null
                                ? 0.0
                                : monthlyDistance
                )
                .build();
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

    private RunRecord createRunRecord(RunRecordRequestDTO dto, Course course) {
        RunRecord runRecod =
                RunRecord.builder()
                        .userId(dto.getUserId())
                        .course(course)
                        .startTime(dto.getStartTime())
                        .endTime(dto.getEndTime())
                        .distanceKm(dto.getDistanceKm())
                        .durationSec(dto.getDurationSec())
                        .build();

        return runRecordRepository.save(runRecod);
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
                .courseName(course.getType())
                .distanceKm(record.getDistanceKm())
                .durationSec(record.getDurationSec())
                .avgPace(record.getAvgPace())
                .coursePath(coursePath)
                .actualPath(actualPath)
                .build();
    }

    private List<RunRecordListResponseDTO.PointDto> parseCoursePath(String pathDataJson) {
        if (pathDataJson == null || pathDataJson.isBlank()) {
            return List.of();
        }
        try {
            PathData pathData = objectMapper.readValue(pathDataJson, PathData.class);
            return downsample(pathData.points(), 50).stream()
                    .map(p -> new RunRecordListResponseDTO.PointDto(p.lat(), p.lng()))
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("코스 pathData 파싱 실패", e);
        }
    }

    private List<RunRecordDetailResponseDTO.PointDto> parseCoursePathFull(String pathDataJson) {
        if(pathDataJson == null || pathDataJson.isBlank()) {
            return List.of();
        }
        try {
            PathData pathData = objectMapper.readValue(pathDataJson, PathData.class);
            return pathData.points().stream()
                    .map(p -> new RunRecordDetailResponseDTO.PointDto(p.lat(), p.lng()))
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("코스 pathData 파싱 실패", e);
        }
    }

    private <T> List<T> downsample(List<T> points, int targetSize) {
        if (points == null || points.size() <= targetSize) {
            return points;
        }

        int step = points.size() / targetSize;
        List<T> result = new ArrayList<>();
        for (int i = 0; i < points.size(); i += step) {
            result.add(points.get(i));
        }

        T lastSource = points.get(points.size() - 1);
        if (result.get(result.size() - 1) != lastSource) {
            result.add(lastSource);
        }
        return result;
    }
}
