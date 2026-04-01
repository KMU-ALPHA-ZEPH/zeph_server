package zeph_server.course.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeph_server.course.domain.Course;
import zeph_server.course.dto.CreateCourseRequest;
import zeph_server.course.dto.RecommendCourseRequest;
import zeph_server.course.dto.common.PathData;
import zeph_server.course.repository.CourseRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)


public class CourseService {
    private final CourseRepository courseRepository
    private final ObjectMapper objectMapper;

    public void recommendCourse(RecommendCourseRequest requestDTO) {

    }

    @Transactional
    public void createCourse(CreateCourseRequest requestDTO) {
        String pathDataJson = convertPathDataToJson(requestDTO.pathData());
        Course course = Course.builder()
                .type(requestDTO.type())
                .distanceKm(requestDTO.distanceKm())
                .pathData(pathDataJson)
                .createdAt(requestDTO.createdAt())
                .build();

        courseRepository.save(course);
    }

    private String convertPathDataToJson(PathData pathData) {
        try {
            return objectMapper.writeValueAsString(pathData);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("pathData를 JSON 문자열로 변환하는 데 실패했습니다.", e);
        }
    }
}

