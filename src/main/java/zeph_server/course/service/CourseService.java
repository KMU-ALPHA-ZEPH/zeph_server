package zeph_server.course.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeph_server.course.domain.Course;
import zeph_server.course.dto.CourseDetailResponse;
import zeph_server.course.dto.CourseResponse;
import zeph_server.course.dto.CreateCourseRequest;
import zeph_server.course.dto.RecommendCourseRequest;
import zeph_server.course.dto.common.PathData;
import zeph_server.course.dto.common.Point;
import zeph_server.course.repository.CourseRepository;

import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)


public class CourseService {
    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    public List<CourseResponse> getAllCourses(String region, String type) {
        List<Course> courses;

        boolean hasRegion = region != null && !region.isBlank();
        boolean hasType = type != null && !type.isBlank();

        if (hasType && hasRegion) {
            courses = courseRepository.findByRegionAndType(type, region);
        } else if (hasType) {
            courses = courseRepository.findByType(type);
        } else if (hasRegion) {
            courses = courseRepository.findByRegion(region);
        } else {
            courses = courseRepository.findAll();
        }

        return courses.stream()
                .map(this::getCourseDisplay)
                .toList();
    }

    public CourseDetailResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 코스를 찾을 수 없습니다."));

        return getCourseDetails(course);
    }

    public void recommendCourse(RecommendCourseRequest requestDTO) {
    }


    @Transactional
    public void createCourse(CreateCourseRequest requestDTO) {
        PathData pathData = requestDTO.pathData();
        Point first = pathData.points().get(0);

        Float startLat = first.lat();
        Float startLng = first.lng();
        // pathData 객체를 JSON 문자열으로 바꿈
        String pathDataJson;
        try {
            pathDataJson = objectMapper.writeValueAsString(pathData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Course course = Course.builder()
                .type(requestDTO.type())
                .distanceKm(requestDTO.distanceKm())
                .roundTrip(requestDTO.roundTrip())
                .pathData(pathDataJson)
                .startLat(startLat)
                .startLng(startLng)
                .createdAt(requestDTO.createdAt())
                .build();
        courseRepository.save(course);
    }


    public CourseResponse getCourseDisplay(Course course) {
        return CourseResponse.create(course);
    }

    public CourseDetailResponse getCourseDetails(Course course) {
        PathData pathData;

        try {
            pathData = objectMapper.readValue(course.getPathData(), PathData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return CourseDetailResponse.create(course, pathData);
    }

}